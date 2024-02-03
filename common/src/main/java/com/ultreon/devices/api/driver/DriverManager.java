package com.ultreon.devices.api.driver;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

import java.util.Optional;

public class DriverManager {
    private final Long2ObjectMap<DeviceDriver> registry = new Long2ObjectArrayMap<>();

    public long registerDriver(DeviceDriver driver) {
        long id = driver.createId();
        registry.put(id, driver);
        return id;
    }

    public DeviceDriver get(long id) {
        return registry.get(id);
    }

    public <T extends DeviceDriver> Optional<T> getByClass(Class<T> driverClass) {
        for (DeviceDriver driver : registry.values()) {
            if (driver.getClass().equals(driverClass)) {
                return Optional.of(driverClass.cast(driver));
            }
        }
        return Optional.empty();
    }

    public <T extends DeviceDriver> Optional<T> getBySubClass(Class<T> driverClass) {
        for (DeviceDriver driver : registry.values()) {
            if (driverClass.isAssignableFrom(driver.getClass())) {
                return Optional.of(driverClass.cast(driver));
            }
        }
        return Optional.empty();
    }
}
