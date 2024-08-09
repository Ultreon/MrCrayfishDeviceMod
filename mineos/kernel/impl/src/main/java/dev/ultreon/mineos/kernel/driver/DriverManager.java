package dev.ultreon.mineos.kernel.driver;

import java.util.Collection;

public interface DriverManager {
    <T> T getDriver(Class<T> clazz);

    <T> Collection<T> getDrivers(Class<T> clazz);

    <T> T getDriver(Class<T> clazz, String name);
}
