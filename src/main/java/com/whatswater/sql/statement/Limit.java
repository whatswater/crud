package com.whatswater.sql.statement;

public class Limit {
    private long offset;
    private int size;

    public Limit() {

    }

    public Limit(int size) {
        this.size = size;
    }

    public Limit(long offset, int size) {
        this.offset = offset;
        this.size = size;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
