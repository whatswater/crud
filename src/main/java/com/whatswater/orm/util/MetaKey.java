package com.whatswater.orm.util;


import java.util.Objects;

public class MetaKey<T> implements Comparable<MetaKey<?>> {
    private String name;
    private MetaKey(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(MetaKey<?> o) {
        if (this == o) return 0;
        if (o == null) return 1;
        return this.name.compareTo(o.name);
    }

    public static <T> MetaKey<T> of(String name) {
        return new MetaKey<>(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaKey)) return false;
        MetaKey<?> metaKey = (MetaKey<?>) o;
        return Objects.equals(name, metaKey.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
