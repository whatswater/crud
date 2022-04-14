package com.whatswater.curd.project.module.todo;

import java.util.Map;


public class TodoClassLoader extends ClassLoader {
    private Map<String, byte[]> data;

    public TodoClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                try {
                    c = findClass(name);
                    if (resolve) {
                        resolveClass(c);
                    }
                } catch (ClassNotFoundException e) {
                    // do nothing
                }
                c = super.loadClass(name, resolve);
            }
            return c;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replaceAll("\\.", "/");
        byte[] b = data.get(path);
        if (b == null) {
            throw new ClassNotFoundException("can't find class byte[] data" + name);
        }
        return defineClass(name, b,0, b.length);
    }

    public void setData(Map<String, byte[]> data) {
        this.data = data;
    }
}
