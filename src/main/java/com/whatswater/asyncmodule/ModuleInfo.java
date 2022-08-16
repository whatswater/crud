package com.whatswater.asyncmodule;

import com.whatswater.asyncmodule.LockUtil.TwoLock;
import com.whatswater.asyncmodule.ModuleEvent.ModuleUninstallEvent;
import com.whatswater.asyncmodule.ModuleEvent.RequireMissedEvent;
import com.whatswater.asyncmodule.ModuleEvent.RequireResolvedEvent;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// 线程安全证明
// ModuleInfo中可变的属性有：
// moduleState、exports、consumers、providers
// 写入时均加了写锁，读取时均加了读锁，写锁使用了tryLock不会导致死锁
// 除此外的RecordState，有3处代码可能导致变动，require中由于写锁的存在，不会并发访问；
// cancelRequire和当前状态无关；exportObject使用cas操作。
public class ModuleInfo implements Comparable<ModuleInfo> {
    private final Module moduleInstance;
    private final ModuleSystem factory;
    private final long moduleId;
    private int moduleState;
    private final String modulePath;
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    final Map<String, Object> exports = new TreeMap<>();
    final NavigableMap<Record, AtomicInteger> consumers = new TreeMap<>(Record.CONSUMER_KEY);
    final NavigableMap<Record, AtomicInteger> providers = new TreeMap<>(Record.PROVIDER_KEY);
    final Queue<ModuleEvent> eventQueue = new ConcurrentLinkedQueue<>();

    ModuleInfo(String modulePath, Module moduleInstance, ModuleSystem factory) {
        this.modulePath = modulePath;
        this.factory = factory;
        this.moduleInstance = moduleInstance;
        this.moduleState = ModuleState.RUNNING;
        this.moduleId = factory.nextModuleId();
    }

    public final boolean require(String modulePath) {
        return require(modulePath, ModuleSystem.DEFAULT_NAME);
    }

    public final boolean require(String modulePath, String... requireNames) {
        ModuleInfo provider = factory.getOrCreateModuleInfo(modulePath);
        return this.require(provider, Arrays.asList(requireNames));
    }

    // 当provider正在卸载时，不能被require，应当等待卸载完后在require
    // 当consumer正在卸载时，不能require其他依赖，应当重新实例化
    private boolean require(ModuleInfo provider, Collection<String> requireNames) {
        ModuleInfo consumer = this;
        List<ModuleEvent> eventList = new ArrayList<>();
        try (TwoLock ignored = LockUtil.getTwoLock(provider.lock.writeLock(), consumer.lock.writeLock())) {
            if (provider._isUninstall() || consumer._isUninstall()) {
                return false;
            }
            for (String requireName: requireNames) {
                Record record = new Record(provider, consumer, requireName);
                AtomicInteger oldState = consumer._getProviderState(record);
                // 此处确实是比较引用
                if (oldState != provider._getConsumerState(record)) {
                    return false;
                }

                AtomicInteger state;
                if (oldState == null) {
                    state = new AtomicInteger(RecordState.STATE_INITIAL);
                    consumer._setProviderState(record, state);
                    provider._setConsumerState(record, state);
                } else if (oldState.get() == RecordState.STATE_CANCELED || oldState.get() == RecordState.STATE_INITIAL) {
                    oldState.set(RecordState.STATE_INITIAL);
                    state = oldState;
                } else {
                    return false;
                }

                Object obj = provider._getExportedObject(requireName);
                if (obj != null) {
                    eventList.add(new RequireResolvedEvent(consumer, provider, requireName, obj));
                    state.set(RecordState.STATE_RESOLVED);
                }
            }
        }
        getFactory().submitModuleEvent(consumer, eventList);
        return true;
    }

    public final boolean cancelRequire(String modulePath, String requireName) {
        ModuleInfo provider = factory.getModuleInfo(modulePath);
        return cancelRequire(provider, requireName);
    }

    private final boolean cancelRequire(ModuleInfo provider, String requireName) {
        ModuleInfo consumer = this;
        try (TwoLock ignored = LockUtil.getTwoLock(provider.lock.readLock(), consumer.lock.readLock())) {
            if (provider._isUninstall() || consumer._isUninstall()) {
                return false;
            }
            Record record = new Record(provider, consumer, requireName);
            AtomicInteger atomicInteger = consumer._getProviderState(record);
            if (atomicInteger == null) {
                return false;
            }
            atomicInteger.set(RecordState.STATE_CANCELED);
        }
        return true;
    }

    public void exportObject(Object obj) {
        exportObject(ModuleSystem.DEFAULT_NAME, obj);
    }

    public void exportObject(String requireName, Object obj) {
        ModuleInfo provider = this;
        Lock writeLock = provider.lock.writeLock();
        Lock readLock = provider.lock.readLock();
        writeLock.lock();
        try {
            if (provider._isUninstall()) {
                return;
            }
            provider._putExportedObject(requireName, obj);
        } finally {
            readLock.lock();
            writeLock.unlock();
        }

        List<ModuleEvent> moduleEvents = new ArrayList<>();
        // 此时获取的都是读锁，不会造成死锁的情况
        try {
            Record record = Record.createProviderKey(provider, requireName);
            for(Map.Entry<Record, AtomicInteger> entry: provider.consumers.tailMap(record, false).entrySet()) {
                Record key = entry.getKey();
                int v = key.getRequireName().compareTo(requireName);
                if(v != 0) {
                    break;
                }

                ModuleInfo consumer = key.getConsumer();
                Lock cReadLock = consumer.lock.readLock();
                cReadLock.lock();
                try {
                    if (consumer._isUninstall()) {
                        continue;
                    }
                    AtomicInteger state = entry.getValue();
                    int stateValue = state.get();
                    while (stateValue == RecordState.STATE_INITIAL) {
                        boolean result = state.compareAndSet(stateValue, RecordState.STATE_RESOLVED);
                        if (result) {
                            break;
                        }
                        stateValue = state.get();
                    }
                    if (stateValue == RecordState.STATE_INITIAL) {
                        moduleEvents.add(new RequireResolvedEvent(consumer, provider, requireName, obj));
                    }
                } finally {
                    cReadLock.unlock();
                }
            }
        } finally {
            readLock.unlock();
        }
        getFactory().submitModuleEvent(moduleEvents);
    }

    public Object getExportedObject(String name) {
        Lock rLock = lock.readLock();
        rLock.lock();
        try {
            return _getExportedObject(name);
        } finally {
            rLock.unlock();
        }
    }

    public void willNotExport(String... requireNames) {
        willNotExport(Arrays.asList(requireNames));
    }

    public void willNotExport(Collection<String> requireNames) {
        TreeSet<String> requireNameSet = new TreeSet<>(requireNames);

        ModuleInfo provider = this;
        List<ModuleEvent> eventList = new ArrayList<>();
        Lock readLock = provider.lock.readLock();
        readLock.lock();
        try {
            if (provider._isUninstall()) {
                return;
            }

            String min = requireNameSet.first();
            String max = requireNameSet.last();
            Record record = Record.createProviderKey(provider, min);
            for(Map.Entry<Record, AtomicInteger> entry: provider.consumers.tailMap(record, false).entrySet()) {
                Record key = entry.getKey();
                int v = key.getRequireName().compareTo(max);
                if(v > 0) {
                    break;
                }
                if (!requireNameSet.contains(key.getRequireName())) {
                    continue;
                }

                ModuleInfo consumer = key.getConsumer();
                Lock cReadLock = consumer.lock.readLock();
                cReadLock.lock();
                try {
                    if (consumer._isUninstall()) {
                        continue;
                    }
                    eventList.add(new RequireMissedEvent(key.getConsumer(), provider, key.getRequireName()));
                } finally {
                    cReadLock.unlock();
                }
            }
        } finally {
            readLock.unlock();
        }
        getFactory().submitModuleEvent(eventList);
    }

    public void uninstallModule() {
        ModuleInfo moduleInfo = this;
        Lock writeLock = moduleInfo.lock.writeLock();
        Lock readLock = moduleInfo.lock.readLock();

        writeLock.lock();
        try {
            moduleInfo.moduleState = ModuleState.UNINSTALLED;
        } finally {
            readLock.lock();
            writeLock.unlock();
        }

        Set<ModuleInfo> consumers = new TreeSet<>();
        try {
            for(Map.Entry<Record, AtomicInteger> entry: moduleInfo.consumers.entrySet()) {
                Record record = entry.getKey();
                ModuleInfo consumer = record.getConsumer();
                consumers.add(consumer);
            }
        } finally {
            readLock.unlock();
        }

        for(ModuleInfo consumer: consumers) {
            Lock consumerReadLock = consumer.lock.readLock();
            consumerReadLock.lock();
            List<ModuleEvent> eventList = new ArrayList<>(1);
            try {
                if (consumer._isUninstall()) {
                    eventList.add(new ModuleUninstallEvent(consumer, moduleInfo));
                }
            } finally {
                consumerReadLock.unlock();
            }
            getFactory().submitModuleEvent(eventList);
        }

        for(Map.Entry<Record, AtomicInteger> entry: moduleInfo.consumers.entrySet()) {
            Record record = entry.getKey();
            ModuleInfo consumer = record.getConsumer();
            Lock cWriteLock = consumer.lock.writeLock();
            cWriteLock.lock();
            try {
                if (consumer._isUninstall()) {
                    continue;
                }
                consumer.providers.remove(record);
            } finally {
                cWriteLock.unlock();
            }
        }
        for(Map.Entry<Record, AtomicInteger> entry: moduleInfo.providers.entrySet()) {
            Record record = entry.getKey();
            ModuleInfo provider = record.getProvider();
            Lock pWriteLock = provider.lock.writeLock();
            pWriteLock.lock();
            try {
                if (provider._isUninstall()) {
                    continue;
                }
                provider.consumers.remove(record);
            } finally {
                pWriteLock.unlock();
            }
        }
        factory.removeModuleInfo(modulePath);
    }

    public int getModuleState() {
        Lock rLock = lock.readLock();
        rLock.lock();
        try {
            return moduleState;
        } finally {
            rLock.unlock();
        }
    }

    public ModuleSystem getFactory() {
        return factory;
    }

    public String getModulePath() {
        return modulePath;
    }

    public long getModuleId() {
        return moduleId;
    }

    private boolean _isUninstall() {
        return moduleState == ModuleState.UNINSTALLED;
    }

    private Object _getExportedObject(String name) {
        return exports.get(name);
    }

    private void _putExportedObject(String name, Object obj) {
        exports.put(name, obj);
    }

    private AtomicInteger _getConsumerState(Record record) {
        return consumers.get(record);
    }

    private void _setConsumerState(Record record, AtomicInteger state) {
        consumers.put(record, state);
    }

    private AtomicInteger _getProviderState(Record record) {
        return providers.get(record);
    }

    private void _setProviderState(Record record, AtomicInteger state) {
        providers.put(record, state);
    }

    public Module getModuleInstance() {
        return this.moduleInstance;
    }

    public Queue<ModuleEvent> getEventQueue() {
        return eventQueue;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.moduleId);
    }

    @Override
    public boolean equals(Object object) {
        return this == object;
    }

    @Override
    public int compareTo(ModuleInfo o) {
        long v = this.moduleId - o.moduleId;
        if(v == 0) {
            return 0;
        }
        if(v > 0) {
            return 1;
        }
        return -1;
    }
}
