package com.whatswater.asyncmodule.util;

import java.util.Arrays;
import java.util.Iterator;


/**
 * @author maxwell
 * 使用数组存储的跳表实现
 * head -----------> 5 ----------------> 10 -------------> null
 * head -> 1 -> 2 -> 5 -> 6 -> 7 -> 8 -> 10 -> 11 -> 13 -> null
 * 每个节点除了保存数据之外，还保存了一个可变数组，数组第一项是跳表节点
 * 第一层的下一个节点，第二项为跳表节点第二层的下一个节点。以上图所示的
 * 跳表为例，值为5的节点，数组第一项是节点6，第二项是节点10，节点高度为
 * 2，数组长度可能大于2，但是只有第一个和第二个有效。
 *
 * 当向跳表中insert数据时，从head节点开始，先从最顶层的链表查找到小于等
 * 于需要insert的数据的最大节点，然后从这个节点开始，查下一层节点。
 * 例如：insert 4，head -> 2
 * @param <E>
 */
public class SkipList<E extends Comparable<E>> implements Iterable<E> {
    public static final int INDEX_MAX_SIZE = 1024;
    public static final Node[] INDEX_EMPTY = new Node[0];
    public static final int MAX_SPAN = 6;
    public static final int MIN_SPAN = 2;

    private final Node<E> head = new Node<>(null, INDEX_EMPTY);
    private int size = 0;

    public void put(E value) {
        int level = head.getHeight() - 1;
        Node<E> current = head;

        Path<Node<E>> path = null;
        while(level >= 0) {
            for(;;) {
                Node<E> node = current.getNextNode(level);
                if(node == null) {
                    path = new Path<>(current, path);
                    level--;
                    break;
                }

                int v = value.compareTo(node.value);
                if(v == 0) {
                    return;
                }
                if(v < 0) {
                    path = new Path<>(current, path);
                    level--;
                    break;
                }
                current = node;
            }
        }

        Node<E> newNode = new Node<>(value, current.getNextNode());
        current.setNextNode(newNode);
        size++;

        if(path != null) {
            addIndex(path.prev);
        }
    }

    private void addNode(Node<E> node, int level, int span) {
        Node<E> nextNode = node.skipStep(level - 1, span / 2 + 1);
        nextNode.setNextNode(node.getNextNode(level), level);
        node.setNextNode(nextNode, level);
    }

    private void addIndex(Path<Node<E>> path) {
        int level = 1;
        while(path != null) {
            Node<E> node = path.value;
            int span = node.calSpan(level);
            if(span <= MAX_SPAN) {
                return;
            }

            addNode(node, level, span);
            path = path.prev;
            level++;
        }

        int topSpan = head.calSpan(level);
        if(topSpan > MAX_SPAN) {
            addNode(head, level, topSpan);
        }
    }

    public boolean contains(E value) {
        int level = head.getHeight() - 1;

        Node<E> current = head;
        while(level >= 0) {
            for(;;) {
                Node<E> node = current.getNextNode(level);
                if(node == null) {
                    level--;
                    break;
                }

                int v = value.compareTo(node.value);
                if(v == 0) {
                    return true;
                }
                if(v < 0) {
                    level--;
                    break;
                }
                current = node;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    private void deleteIndex(Path<Node<E>> path, int deleteHeight) {
        int level = deleteHeight;
        while(path != null) {
            Node<E> node = path.value;

            Node<E> next = node.getNextNode(level);
            if(next == null) {
                if(node == head) {
                    head.deleteIndex(level);
                }
                return;
            }

            Node<E> lowLevelNext = node.getNextNode(level - 1);
            int span = 0;
            while(lowLevelNext != next) {
                span++;
                lowLevelNext = lowLevelNext.getNextNode(level - 1);
                if(span >= MIN_SPAN) {
                    return;
                }
            }

            int height = next.getHeight();
            int remainLen = level;
            while(level < height - 1) {
                node.setNextNode(next.getNextNode(level), level);
                int newSpan = node.calSpan(level);
                if(newSpan > MAX_SPAN) {
                    addNode(node, level, newSpan);
                }

                path = path.prev;
                node = path.value;
                level++;
            }
            node.setNextNode(next.getNextNode(level), level);
            int newSpan = node.calSpan(level);

            if(newSpan > MAX_SPAN) {
                addNode(node, level, newSpan);
                next.deleteIndex(remainLen);
                return;
            }
            else {
                path = path.prev;
                level++;
                next.deleteIndex(remainLen);
            }
        }
    }

    public void delete(E value) {
        int level = head.getHeight() - 1;
        Path<Node<E>> path = null;
        Node<E> current = head;
        while(level >= 0) {
            for(;;) {
                Node<E> node = current.getNextNode(level);
                if(node == null) {
                    path = new Path<>(current, path);
                    level--;
                    break;
                }

                int v = value.compareTo(node.value);
                if(v == 0) { // 找到要删除的Node
                    int height = level + 1;
                    current.setNextNode(node.getNextNode(level), level);

                    if(level > 0) {
                        path = new Path<>(current, path);
                        do {
                            level--;
                            Node<E> tmp = current.getNextNode(level);
                            int v2 = tmp.value.compareTo(node.value);

                            while(v2 < 0) {
                                // current = tmp必需放在循环内，此循环执行完后v2其实已经等于0，这时的tmp等于node
                                current = tmp;
                                tmp = tmp.getNextNode(level);
                                v2 = tmp.value.compareTo(node.value);
                            }
                            current.setNextNode(node.getNextNode(level), level);
                            path = new Path<>(current, path);
                        }
                        while(level > 0);

                        path = path.prev;
                        for(level = 1; level < height - 1; level++) {
                            Node<E> pathNode = path.value;
                            int span = pathNode.calSpan(level);
                            if(span > MAX_SPAN) {
                                addNode(pathNode, level, span);
                            }
                            path = path.prev;
                        }
                        Node<E> pathNode = path.value;
                        int span = pathNode.calSpan(level);
                        if(span > MAX_SPAN) {
                            addNode(pathNode, level, span);
                            return;
                        }
                        path = path.prev;
                    }

                    deleteIndex(path, height);
                    return;
                }

                if(v < 0) {
                    path = new Path<>(current, path);
                    level--;
                    break;
                }
                current = node;
            }
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new NodeIterator<>(head);
    }

    public static class Node<E> {
        final E value;
        Node[] index;
        int height;

        Node(E value, Node<E> next) {
            this.value = value;
            if(next == null) {
                this.index = INDEX_EMPTY;
            }
            else {
                this.index = new Node[] { next };
                height = 1;
            }
        }

        Node(E value, Node[] index) {
            this.value = value;
            this.index = index;
        }

        int getHeight() {
            return height;
        }

        Node<E> skipStep(int level, int step) {
            Node<E> node = getNextNode(level);
            while(node != null && step > 0) {
                node = node.getNextNode(level);
                step--;
            }
            return node;
        }

        @SuppressWarnings("unchecked")
        Node<E> getNextNode(int level) {
            if(level >= height) {
                return null;
            }
            return index[level];
        }

        Node<E> getNextNode() {
            return getNextNode(0);
        }

        void setNextNode(Node<E> node) {
            setNextNode(node, 0);
        }

        void setNextNode(Node<E> node, int level) {
            int contentSize = level + 1;
            ensureCapacity(contentSize);
            if(contentSize > height) {
                height = contentSize;
            }
            index[level] = node;
        }

        void deleteIndex(int remainHeight) {
            if(remainHeight % 2 == 0) {
                index = Arrays.copyOf(index, remainHeight + 1);
                index[index.length - 1] = null;
            }
            else {
                index = Arrays.copyOf(index, remainHeight);
            }
            height = remainHeight;
        }

        private void ensureCapacity(int size) {
            if(size > index.length) {
                if(size > INDEX_MAX_SIZE) {
                    throw new RuntimeException("index max size");
                }
                // 此处不需要校验size+1和INDEX_MAX_SIZE的大小，超一点不重要
                index = Arrays.copyOf(index, size + 1);
            }
        }

        /**
         * 获取本节点某一层之间的节点数
         * @param level 层，需大于等于1
         * @return 跨度
         */
        int calSpan(int level) {
            Node<E> next = getNextNode(level);
            Node<E> lowLevelNext = getNextNode(level - 1);

            int span = 0;
            while(lowLevelNext != next) {
                span++;
                lowLevelNext = lowLevelNext.getNextNode(level - 1);
            }
            return span;
        }
    }

    private static class NodeIterator<E> implements Iterator<E> {
        Node<E> node;

        NodeIterator(Node<E> node) {
            this.node = node;
        }

        @Override
        public boolean hasNext() {
            return node.getNextNode() != null;
        }

        @Override
        public E next() {
            node = node.getNextNode();
            return node.value;
        }
    }

    private static class Path<E> {
        E value;
        Path<E> prev;

        Path(E value) {
            this.value = value;
        }

        Path(E value, Path<E> prev) {
            this.value = value;
            this.prev = prev;
        }
    }

    private String logPath(String prefix, Path<Node<E>> path) {
        StringBuilder info = new StringBuilder(prefix);
        while(path != null) {
            Node<E> node = path.value;
            if(node == head) {
                info.append("head").append(" <- ");
            }
            else {
                info.append(node.value).append(" <- ");
            }
            path = path.prev;
        }
        if(info.substring(info.length() - 4).equals(" <- ")) {
            info.setLength(info.length() - 4);
        }

        return info.toString();
    }

    public void printGraph() {
        int len = head.getHeight();

        StringBuilder graph = new StringBuilder("graph:\n");
        for(int i = len - 1; i >= 0; i--) {
            graph.append("head -> ");
            Node<E> node = head.getNextNode(i);
            while(node != null) {
                graph.append(node.value).append(" -> ");
                node = node.getNextNode(i);
            }
            graph.append("null\n");
        }
        System.out.println(graph.toString());
    }

    public static <E> int pathCount(Path<E> path) {
        int count = 0;
        while(path != null) {
            count++;
            path = path.prev;
        }
        return count;
    }

    public boolean check() {
        int len = head.getHeight();

        Node<E> node1;
        Node<E> node2;
        for(int i = 0; i < len - 1; i++) {
            node1 = head.getNextNode(i);
            node2 = head.getNextNode(i + 1);

            while(node2 != null) {
                E value = node2.value;

                while(node1 != null) {
                    int v1 = node1.value.compareTo(value);
                    if(v1 < 0) {
                        node1 = node1.getNextNode(i);
                        continue;
                    }

                    if(v1 > 0) {
                        System.out.println("low level value: " + node1.value + ", high level value: " + value);
                        return false;
                    }
                    break;
                }
                node2 = node2.getNextNode(i + 1);
            }
        }
        return true;
    }
}
