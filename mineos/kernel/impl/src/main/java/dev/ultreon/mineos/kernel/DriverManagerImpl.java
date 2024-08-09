package dev.ultreon.mineos.kernel;

import dev.ultreon.mineos.kernel.driver.DriverManager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DriverManagerImpl implements DriverManager {
    Map<String, Object> drivers = new ConcurrentHashMap<>();

    public <T> void register(String vga, T driver) {
        this.drivers.put(vga, driver);
    }

    @Override
    public <T> T getDriver(Class<T> clazz) {
        for (Map.Entry<String, Object> entry : drivers.entrySet()) {
            if (clazz.isInstance(entry.getValue())) {
                return clazz.cast(entry.getValue());
            }
        }
        return null;
    }

    @Override
    public <T> Collection<T> getDrivers(Class<T> clazz) {
        Collection<T> list = new java.util.ArrayList<>();
        for (Map.Entry<String, Object> entry : drivers.entrySet()) {
            if (clazz.isInstance(entry.getValue())) {
                list.add(clazz.cast(entry.getValue()));
            }
        }
        return list;
    }

    @Override
    public <T> T getDriver(Class<T> clazz, String name) {
        T result = null;
        Object obj = drivers.get(name);
        if (clazz.isInstance(obj)) {
            result = clazz.cast(obj);
        }
        return result;
    }
}
