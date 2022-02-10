package com.whatswater.asyncmodule;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ModuleEventExecutorTest {
    public static void main(String[] args) {
        SimpleEventExecutor executor = new SimpleEventExecutor(10, Executors.defaultThreadFactory());

        for (int i = 0; i < 10; i++) {
            int key = i;
            new Thread(() -> {
                Consumer consumer = new Consumer();
                consumer.setKey(String.valueOf(key));

                for (int j = 0; j < 50; j++) {
                    executor.submitTask(consumer, Arrays.asList(new SimpleEvent(executor, consumer), new SimpleEvent(executor, consumer)));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    static class Consumer {
        private String key;
        final Queue<SimpleEvent> eventQueue = new ConcurrentLinkedQueue<>();

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Queue<SimpleEvent> getEventQueue() {
            return eventQueue;
        }
    }

    static class SimpleEventExecutor {
        Worker[] workers;
        public SimpleEventExecutor(int coreSize, ThreadFactory threadFactory) {
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

        public void submitTask(Consumer consumer, List<SimpleEvent> eventList) {
            // double check
            Worker worker = findBindWorker(consumer);
            if (worker == null) {
                synchronized (this) {
                    worker = findBindWorker(consumer);
                    if (worker == null) {
                        Worker newWorker = selectWorker();
                        newWorker.bind(consumer, eventList);
                        if (newWorker.idle) {
                            LockSupport.unpark(newWorker.thread);
                        }
                    }
                }
            } else {
                worker.bind(consumer, eventList);
                if (worker.idle) {
                    LockSupport.unpark(worker.thread);
                }
            }
        }

        Worker findBindWorker(Consumer consumer) {
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
    }



    static class Worker implements Runnable {
        // consumers只读，无线程安全问题
        // 若因为consumers效率问题，Worker内部可记录当前消费的偏移值
        List<Consumer> consumers = new ArrayList<>();
        // 处于等待队列的消费者
        List<Consumer> waitConsumer = new ArrayList<>();
        final ReadWriteLock lock = new ReentrantReadWriteLock();
        Thread thread;

        // inSize - deSize 当前剩余事件数
        volatile int inSize = 0;
        volatile int deSize = 0;
        volatile boolean idle = true;

        void bind(Consumer consumer, List<SimpleEvent> eventList) {
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

        boolean contains(Consumer consumer) {
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
        SimpleEvent nextEvent() {
            SimpleEvent event = null;

            Lock readLock = lock.readLock();
            readLock.lock();
            try {
                for (Consumer consumer: consumers) {
                    Queue<SimpleEvent> queue = consumer.getEventQueue();
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
                SimpleEvent event = nextEvent();
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


    static final AtomicInteger executeCount = new AtomicInteger();
    static final AtomicInteger submitCount = new AtomicInteger(1000);
    static class SimpleEvent {
        private final SimpleEventExecutor simpleEventExecutor;
        private final Consumer consumer;
        private final String eventIndex;

        public SimpleEvent(SimpleEventExecutor simpleEventExecutor, Consumer consumer) {
            this.simpleEventExecutor = simpleEventExecutor;
            this.consumer = consumer;
            this.eventIndex = UUID.randomUUID().toString();
        }

        void execute() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("consumerKey: %s, eventIndex: %s%n", consumer.getKey(), eventIndex);
            System.out.printf("submitCount: %d, executeCount: %d%n", submitCount.get(), executeCount.incrementAndGet());
            int v = ThreadLocalRandom.current().nextInt(12);
            if (v >= 8) {
                simpleEventExecutor.submitTask(consumer, Arrays.asList(new SimpleEvent(simpleEventExecutor, consumer), new SimpleEvent(simpleEventExecutor, consumer)));
                submitCount.addAndGet(2);
            }
        }
    }
}
