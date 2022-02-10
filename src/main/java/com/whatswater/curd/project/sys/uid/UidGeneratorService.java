package com.whatswater.curd.project.sys.uid;


/**
 * id组成：sign + second + back + workerId + sequence
 */
public class UidGeneratorService {
    public final static long START_SECOND = 1634469851L;

    // 回拨位数
    private final static long BACK_BIT = 3;
    // worker位数
    private final static long WORKER_BIT = 10;
    // 序列号位数
    private final static long SEQUENCE_BIT = 18;
    private final static long SECOND_BIT = 32;

    // 序列号最大值
    public final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    // worker最大值
    public final static long MAX_WORKER = ~(-1L << WORKER_BIT);
    // 回拨数最大值
    public final static long MAX_BACK = ~(-1L << BACK_BIT);
    // 秒数最大值
    public final static long MAX_SECOND = ~(-1L << SECOND_BIT);


    // 移位值
    public final static long SEQUENCE_LEFT = 0L;
    public final static long WORK_LEFT = SEQUENCE_BIT + SEQUENCE_LEFT;
    public final static long BACK_LEFT = WORKER_BIT + WORK_LEFT;
    public final static long SECOND_LEFT = BACK_BIT + BACK_LEFT;


    // 机器Id
    private final long workerId;
    // 上次的时间戳
    private long lastEpoch = -1L;
    // 序列号
    private long sequence = 0L;
    // 回拨序号
    private long back = 0L;

    public UidGeneratorService(int workerId) {
        if (workerId > MAX_WORKER) {
            throw new RuntimeException("workerId > MAX_WORKER");
        }
        this.workerId = workerId;
    }

    public UidGeneratorService(int workerId, int back) {
        if (workerId > MAX_WORKER) {
            throw new RuntimeException("workerId > MAX_WORKER");
        }
        if (back > MAX_BACK) {
            throw new RuntimeException("back > MAX_BACK");
        }
        this.workerId = workerId;
        this.back = back;
    }

    public synchronized long nextId() {
        long epoch = nextSecond();
        if (epoch > MAX_SECOND) {
            throw new RuntimeException("Generator dead! current time > MAX_SECOND");
        }

        // 设置回拨数
        if (epoch < lastEpoch) {
            if (back >= MAX_BACK) {
                back = 0;
            } else {
                back++;
            }
            lastEpoch = epoch;
        }

        if (epoch == lastEpoch) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                epoch = waitNextSecond();
            }
        } else {
            sequence = 0L;
        }

        lastEpoch = epoch;
        return (epoch - START_SECOND) << SECOND_LEFT
            | back << BACK_LEFT
            | workerId << WORK_LEFT
            | sequence << SEQUENCE_LEFT;
    }

    // 一直运行，直到大于lastEpoch
    private long waitNextSecond() {
        long second = nextSecond();
        while (second <= lastEpoch) {
            second = nextSecond();
        }
        return second;
    }

    private long nextSecond() {
        return System.currentTimeMillis() / 1000L;
    }
}
