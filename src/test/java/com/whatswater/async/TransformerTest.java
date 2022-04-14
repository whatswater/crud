package com.whatswater.async;


import com.zandero.utils.Pair;
import io.vertx.core.Future;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TransformerTest extends Object {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, AnalyzerException, NoSuchFieldException {
        Transformer transformer = new Transformer("com/whatswater/async/AwaitCall");
//        Pair<String, byte[]> data = transformer.transform();
//
//        MyClassLoader classLoader = new MyClassLoader(data, TransformerTest.class.getClassLoader());
//        Class<?> t = classLoader.loadClass(data.getKey().replaceAll("/", "."));
//
//        Object o = t.newInstance();
//        Method method = t.getDeclaredMethod("moveToNext", int.class);
//        method.invoke(o, 0);
//
//        Field field = t.getDeclaredField("_future");
//        Object v = field.get(o);
//        if (v instanceof Future) {
//            Future<String> f = (Future<String>) v;
//            f.onSuccess(System.out::println);
//        }
//
//        System.out.println("first output!!!");
    }

    public static class MyClassLoader extends ClassLoader {
        private Pair<String, byte[]> data;

        public MyClassLoader(Pair<String, byte[]> data, ClassLoader parent) {
            super(parent);
            this.data = data;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.equals(data.getKey().replaceAll("/", "."))) {
                return defineClass(name, data.getValue(),0, data.getValue().length);
            }
            return null;
        }
    }
}
