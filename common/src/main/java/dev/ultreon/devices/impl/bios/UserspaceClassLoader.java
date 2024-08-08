package dev.ultreon.devices.impl.bios;

import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserspaceClassLoader extends ClassLoader {
    private final IsolatedClassLoader parent;
    private final VBios vBios;
    private final Executable executable;

    private final Map<String, Class<?>> classes = new HashMap<>();

    public UserspaceClassLoader(IsolatedClassLoader parent, VBios vBios, Executable executable) {
        this.parent = parent;
        this.vBios = vBios;
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
