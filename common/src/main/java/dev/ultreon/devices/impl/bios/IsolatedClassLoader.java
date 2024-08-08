package dev.ultreon.devices.impl.bios;

import java.net.URL;
import java.net.URLClassLoader;

public class IsolatedClassLoader extends URLClassLoader {
    private final ClassLoader parent;
    private final String packageName;

    public IsolatedClassLoader(ClassLoader parent, URL url, String packageName) {
        super(new URL[]{url}, parent);
        this.parent = parent;
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.startsWith("dev.ultreon.devices.api.bios.")) {
            return parent.loadClass(name);
        }
        if (name.startsWith(packageName)) {
            return super.findClass(name);
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith("dev.ultreon.devices.api.bios.")) {
            return parent.loadClass(name);
        }
        if (name.startsWith(packageName)) {
            return super.loadClass(name);
        }
        throw new ClassNotFoundException(name);
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }
}
