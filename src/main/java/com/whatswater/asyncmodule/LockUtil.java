package com.whatswater.asyncmodule;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

public class LockUtil {
    public static final AtomicInteger failCount1 = new AtomicInteger(0);
    public static final AtomicInteger failCount2 = new AtomicInteger(0);
    public static final AtomicInteger failCount3 = new AtomicInteger(0);

    public static int lock(AtomicInteger lock, int... startStates) {
        outer:
        while(true) {
            int value = lock.get();
            int v1 = value & 0xFFFF0000;
            if(v1 == 0) {
                if(startStates != null) {
                    for(int startState: startStates) {
                        if(startState == value) {
                            final boolean b = lock.compareAndSet(value,  0x0001_00000 | value);
                            if(!b) {
                                failCount1.incrementAndGet();
                                continue outer;
                            }
                            return value;
                        }
                    }
                    return -1;
                }
                else {
                    final boolean b = lock.compareAndSet(value,  0x0001_00000 | value);
                    if(!b) {
                        failCount1.incrementAndGet();
                        continue;
                    }
                    return value;
                }
            }
            failCount1.incrementAndGet();
            Thread.yield();
        }
    }

    public static void unLock(AtomicInteger lock, int newState) {
        lock.set(newState);
    }

    public static String padding(String origin, int len) {
        if(origin.length() >= len) {
            return origin;
        }

        StringBuilder zero = new StringBuilder(len);
        for(int i = 0; i < len - origin.length(); i++) {
            zero.append('0');
        }
        zero.append(origin);
        return zero.toString();
    }

    // 最多支持0xFFFE个线程持有读锁，不对读锁的数目做校验，因为这个工具类实现的本来就很烂，也不需要给其他地方用
    public static int readLockAndGet(AtomicInteger lock) {
        while(true) {
            int v = lock.get();
            if((v & 0x0001_0000) == 0) {
                int newValue = v + (1 << 17);
                if(lock.compareAndSet(v, newValue)) {
                    return v & 0x0000_FFFF;
                }
                else {
                    failCount2.incrementAndGet();
                }
            }
            else {
                failCount2.incrementAndGet();
                Thread.yield();
            }
        }
    }

    public static void unLockReadLock(AtomicInteger lock) {
        while(true) {
            int v = lock.get();
            int v1 = v - (1 << 17);
            if(lock.compareAndSet(v, v1)) {
                return;
            }
        }
    }

    public static int writeLockAndGet(AtomicInteger lock) {
        String threadName = Thread.currentThread().getName();
        while(true) {
            int v = lock.get();
            if((v & 0xFFFF_0000) == 0) {
                int newValue = v | 0x0001_0000 + (1 << 17);

                if(lock.compareAndSet(v, newValue)) {
                    return v & 0x0000_FFFF;
                }
                else {
                    failCount3.incrementAndGet();
                }
            }
            else {
                failCount3.incrementAndGet();
                Thread.yield();
            }
        }
    }

    public static boolean writeToReadLock(AtomicInteger lock, int newValue) {
        lock.set((lock.get() & 0xFFFE_0000) | (0x0000_FFFF & newValue));
        return true;
    }

    public static void unLockWriteLock(AtomicInteger lock, int newValue) {
        String threadName = Thread.currentThread().getName();
        lock.set(newValue & 0x0000_FFFF);
    }

    public static TwoStateReadLock getTwoStateReadLock(AtomicInteger lock1, AtomicInteger lock2) {
        while(true) {
            int v1 = lock1.get();
            int v2 = lock2.get();
            if((v1 & 0x0001_0000) == 0 && (v2 & 0x0001_0000) == 0) {
                if(lock1.compareAndSet(v1, v1 + (1 << 17))) {
                    if(lock2.compareAndSet(v2, v2 + (1 << 17))) {
                        return new TwoStateReadLock(lock1, v1 & 0x0000_FFFF, lock2, v2 & 0x0000_FFFF);
                    }
                    unLockReadLock(lock1);
                    AtomicInteger tmp = lock1;
                    lock1 = lock2;
                    lock2 = tmp;
                }
            }
            else {
                Thread.yield();
            }
        }
    }

    public static class TwoStateReadLock implements AutoCloseable {
        final AtomicInteger lock1;
        final int state1;
        final AtomicInteger lock2;
        final int state2;

        public TwoStateReadLock(AtomicInteger lock1, int state1, AtomicInteger lock2, int state2) {
            this.lock1 = lock1;
            this.lock2 = lock2;
            this.state1 = state1;
            this.state2 = state2;
        }
        @Override
        public void close() {
            try {
                unLockReadLock(lock1);
            }
            finally {
                unLockReadLock(lock2);
            }
        }
    }

    public static TwoLock getTwoLock(Lock lock1, Lock lock2) {
        while(true) {
            lock1.lock();
            final boolean lockResult = lock2.tryLock();
            if(lockResult) {
                return new TwoLock(lock2, lock1);
            }
            lock1.unlock();
            Lock t = lock1;
            lock1 = lock2;
            lock2 = t;
        }
    }

    public static TwoLock getTwoLock(Lock lock1, Lock lock2, int max) throws LockException {
        int count = 0;
        while(true) {
            count++;
            lock1.lock();
            final boolean lockResult = lock2.tryLock();
            if(lockResult) {
                return new TwoLock(lock2, lock1);
            }
            lock1.unlock();
            Lock t = lock1;
            lock1 = lock2;
            lock2 = t;

            if(count >= max) {
                throw new LockException();
            }
        }
    }

    public static class LockException extends Exception {

    }

    public static class TwoLock implements AutoCloseable {
        final Lock _0;
        final Lock _1;

        TwoLock(Lock first, Lock second) {
            this._0 = first;
            this._1 = second;
        }

        @Override
        public void close() {
            try {
                _1.unlock();
            }
            finally {
                _0.unlock();
            }
        }
    }
}
