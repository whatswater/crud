package com.whatswater.asyncmodule.util;


import com.whatswater.asyncmodule.ModuleSystem;

import java.util.Objects;

public class Key<T> implements Comparable<Key<?>> {
    private final String modulePath;
    private final String name;

    public Key(String modulePath, String name) {
        this.modulePath = modulePath;
        this.name = name;
    }

    public Key(String modulePath) {
        this.modulePath = modulePath;
        this.name = ModuleSystem.DEFAULT_NAME;
    }

    public String getName() {
        return name;
    }

    public String getModulePath() {
        return modulePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;
        Key<?> key = (Key<?>) o;
        return Objects.equals(getModulePath(), key.getModulePath()) && Objects.equals(getName(), key.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getModulePath(), getName());
    }

    @Override
    public int compareTo(Key<?> o) {
        if (this == o) {
            return 0;
        }
        if (o == null) {
            return 1;
        }

        int v1 = modulePath.compareTo(o.modulePath);
        if (v1 != 0) {
            return v1;
        }
        return name.compareTo(o.name);
    }
}
