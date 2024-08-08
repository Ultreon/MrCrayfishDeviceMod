package dev.ultreon.mineos;

import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppClassLoader extends ClassLoader {
    private final Executable executable;

    private final Map<String, Class<?>> classes = new HashMap<>();

    public AppClassLoader(ClassLoader parent, Executable executable) {
        super(parent);
        this.executable = executable;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.startsWith("java.awt") || !name.startsWith("java.") && name.startsWith("dev.ultreon.devices.")) {
            throw new ClassNotFoundException(name);
        }

        if (classes.containsKey(name)) {
            return classes.get(name);
        }

        if (this.executable == null) {
            throw new IOError(new IOException("Executable is null"));
        }

        byte[] aClass = this.executable.getClass(name);
        if (aClass == null) {
            throw new ClassNotFoundException(name);
        }

        Class<?> definedClass = defineClass(name, aClass, 0, aClass.length);
        classes.put(name, definedClass);
        return definedClass;
    }
}
