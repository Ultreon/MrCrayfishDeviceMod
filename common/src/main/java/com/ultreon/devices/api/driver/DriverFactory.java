package com.ultreon.devices.api.driver;

import java.util.function.Supplier;

public class DriverFactory<T extends DeviceDriver> {
    private final Supplier<T> factory;

    private DriverFactory(Supplier<T> factory) {
        this.factory = factory;
    }

    public T create() {
        return factory.get();
    }

    public static <T extends DeviceDriver> DriverFactory<T> of(Supplier<T> driver) {
        return new DriverFactory<>(driver);
    }
}
