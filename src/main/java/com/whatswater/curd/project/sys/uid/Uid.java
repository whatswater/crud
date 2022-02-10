package com.whatswater.curd.project.sys.uid;


import java.time.*;

public class Uid {
    private final long value;

    public Uid(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public LocalDateTime getCreateTime() {
        ZonedDateTime zonedDateTime = Instant.ofEpochSecond((value >> UidGeneratorService.SECOND_LEFT) + UidGeneratorService.START_SECOND).atZone(ZoneId.systemDefault());
        return zonedDateTime.toLocalDateTime();
    }

    public int getWorkerId() {
        return (int)(value & (UidGeneratorService.MAX_WORKER << UidGeneratorService.WORK_LEFT)) >> UidGeneratorService.WORK_LEFT;
    }

    public int getSequence() {
        return (int)(value & (UidGeneratorService.MAX_SEQUENCE << UidGeneratorService.SEQUENCE_LEFT)) >> UidGeneratorService.SEQUENCE_LEFT;
    }
}
