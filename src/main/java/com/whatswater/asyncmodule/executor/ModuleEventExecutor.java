package com.whatswater.asyncmodule.executor;

import com.whatswater.asyncmodule.ModuleEvent;
import com.whatswater.asyncmodule.ModuleInfo;

import java.util.*;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// 线程缩减实现
// 1、实现一个事件，事件中检查每个线程的idle时间
// 2、当线程是idle时，submitTask时，将此事件添加到线程池
// 3、此事件执行完后，判断当前是否只存在一个线程，若是，则关闭检查；若否，则调用setTimeout注册一个检测事件

// 模块事件执行器
// 提供一个bindWorker方法，由moduleEventExecutor获取一个worker
// 当moduleEventExecutor判断当前consumer无绑定的worker时，从worker池中
// 选择一个任务数最小的线程绑定。
// 当一个线程执行完某个consumer的事件时，自动解绑。
// 当一个线程空闲时，从任务数最多的线程（超过两个consumer，任务数超过10000）迁移consumer到此线程（解绑+重新绑定）。
// 每个worker维护自身剩余任务数量。
// worker提供方法，executeModuleEvent，传入moduleInfo，然后通知线程运行
// 通知线程运行使用LockSupport.unpark
// 暂停线程使用park
// 通知时，判断线程当前是否处于park状态或者即将处于park状态


// Worker线程安全证明
// Worker中Lock用于锁定addConsumers和consumers的交换操作（swap），和inSize、deSize的重置操作（reset）
// addConsumers使用syn控制其访问，而consumers是只读的
// deSize是固定线程修改，且不会和（reset）同时执行
// inSize修改时是单线程的（volatile），执行时获取了Lock的读锁，和reset操作错开
// idle修改时是单线程的（volatile）
public class ModuleEventExecutor {
    Worker[] workers;

    public ModuleEventExecutor(int coreSize, ThreadFactory threadFactory) {
        workers = new Worker[coreSize];
        for (int i = 0; i < coreSize; i++) {
            Worker worker = new Worker();
            worker.thread = threadFactory.newThread(worker);
            workers[i] = worker;
        }

        for (Worker worker: workers) {
            worker.thread.start();
        }
    }

    public void submitTask(ModuleInfo consumer, List<ModuleEvent> eventList) {
        // double check
        Worker worker = findBindWorker(consumer);
        if (worker == null) {
            synchronized (this) {
                worker = findBindWorker(consumer);
                if (worker == null) {
                    worker = selectWorker();
                }
            }
        }
        worker.bind(consumer, eventList);
        if (worker.idle) {
            LockSupport.unpark(worker.thread);
        }
    }

    Worker findBindWorker(ModuleInfo consumer) {
        for (int i = 0; i < workers.length; i++) {
            Worker worker = workers[i];
            Lock readLock = worker.lock.readLock();
            readLock.lock();
            try {
                if (worker.contains(consumer)) {
                    return worker;
                }
            } finally {
                readLock.unlock();
            }
        }
        return null;
    }

    Worker selectWorker() {
        int size = Integer.MAX_VALUE;
        Worker ret = null;
        for (int i = 0; i < workers.length; i++) {
            Worker worker = workers[i];
            if (worker.idle) {
                ret = worker;
                break;
            }

            int tSize = (worker.inSize - worker.deSize);
            if (size > tSize) {
                size = tSize;
                ret = worker;
            }
        }
        return ret;
    }

    static class Worker implements Runnable {
        // consumers只读，无线程安全问题
        // 若因为consumers效率问题，Worker内部可记录当前消费的偏移值
        List<ModuleInfo> consumers = new ArrayList<>();
        // 处于等待队列的消费者
        List<ModuleInfo> waitConsumer = new ArrayList<>();
        final ReadWriteLock lock = new ReentrantReadWriteLock();
        Thread thread;

        // inSize - deSize 当前剩余事件数
        volatile int inSize = 0;
        volatile int deSize = 0;
        volatile boolean idle = true;

        void bind(ModuleInfo consumer, List<ModuleEvent> eventList) {
            Lock readLock = lock.readLock();
            readLock.lock();
            try {
                consumer.getEventQueue().addAll(eventList);
                synchronized (waitConsumer) {
                    // 判断重复
                    waitConsumer.add(consumer);
                    inSize += eventList.size();
                }
            } finally {
                readLock.unlock();
            }
        }

        boolean contains(ModuleInfo consumer) {
            Lock readLock = lock.readLock();
            readLock.lock();
            try {
                synchronized (waitConsumer) {
                    return consumers.contains(consumer) || waitConsumer.contains(consumer);
                }
            } finally {
                readLock.unlock();
            }
        }

        // consumer只会被本线程操作，所以没有线程安全问题
        ModuleEvent nextEvent() {
            ModuleEvent event = null;

            Lock readLock = lock.readLock();
            readLock.lock();
            try {
                for (ModuleInfo consumer: consumers) {
                    Queue<ModuleEvent> queue = consumer.getEventQueue();
                    event = queue.poll();
                    if (event != null) {
                        break;
                    }
                }
            } finally {
                readLock.unlock();
            }
            return event;
        }

        boolean swap() {
            Lock writeLock = lock.writeLock();
            writeLock.lock();
            try {
                consumers = waitConsumer;
                waitConsumer = new ArrayList<>();
                inSize -= deSize;
                deSize = 0;
                return !consumers.isEmpty();
            } finally {
                writeLock.unlock();
            }
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                ModuleEvent event = nextEvent();
                if (event == null) {
                    if(swap()) {
                        continue;
                    }

                    idle = true;
                    // 中断时代码会直接执行下去
                    LockSupport.park();
                } else {
                    idle = false;
                    deSize++;
                    event.execute();
                }
            }
        }
    }

}
