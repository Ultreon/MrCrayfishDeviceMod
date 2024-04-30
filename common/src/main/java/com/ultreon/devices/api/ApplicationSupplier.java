package com.ultreon.devices.api;

import com.ultreon.devices.api.app.Application;

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
