package com.whatswater.asyncmodule.isolation;

/**
 * 模块系统级别的类加载器
 * 和模块系统对象绑定
 */
public class ModuleSystemClassLoader extends ClassLoader {
    public static final String PACKAGE_START = "com.whatswater.asyncmodule";
    private final String packageStart;

    public ModuleSystemClassLoader() {
        super();
        this.packageStart = PACKAGE_START;
    }

    public ModuleSystemClassLoader(ClassLoader parent) {
        super(parent);
        this.packageStart = PACKAGE_START;
    }

    public ModuleSystemClassLoader(String packageStart) {
        super();
        this.packageStart = packageStart;
    }

    public ModuleSystemClassLoader(ClassLoader parent, String packageStart) {
        super(parent);
        this.packageStart = packageStart;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 非moduleSystem的class，通过parent classloader加载
        if (!name.startsWith(packageStart)) {
            return super.loadClass(name, resolve);
        }

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
//        String path = name.replaceAll("\\.", "/");
//        byte[] b = data.get(path);
//        if (b == null) {
//            throw new ClassNotFoundException("can't find class byte[] data" + name);
//        }
//        return defineClass(name, b, 0, b.length);
        return null;
    }
}
