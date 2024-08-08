package com.jab125.classloader.api;

import com.jab125.classloader.impl.CustomClassLoader;

import java.lang.reflect.InvocationTargetException;

public class ClassLoaderApi {
    public static ClassLoaderApi INSTANCE = new ClassLoaderApi();
    public void init() {
        var d = new CustomClassLoader();
        d.cl.getExecutionClasses().forEach((a) -> {
            try {
                var c = d.cl.loadClass(a);
                var cc = c.getAnnotation(Execution.class).value();
                var m = c.getMethod(cc);
                m.invoke(c.newInstance());
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });
    }
}
