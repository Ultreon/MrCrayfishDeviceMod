package dev.ultreon.devices.impl;

import dev.ultreon.mineos.api.Application;

import java.util.function.Supplier;

public interface ApplicationSupplier {

    /**
     * Gets a result.
     *
     * @return a result
     */
    Supplier<Application> get();

    boolean isSystem();
}
