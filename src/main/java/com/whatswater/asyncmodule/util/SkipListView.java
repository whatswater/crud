package com.whatswater.asyncmodule.util;

import java.io.Closeable;
import java.io.IOException;

public class SkipListView<E extends Comparable<E>> implements Closeable {
    private E startValue;
    private E endValue;
    private SkipList.Node<E> startNode;
    private SkipList.Node<E> endNode;

    @Override
    public void close() throws IOException {

    }
}
