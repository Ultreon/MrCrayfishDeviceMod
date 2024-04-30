package com.ultreon.devices.impl;

import java.util.UUID;

public record OsLoadContext(UUID player) {
    private static final ThreadLocal<OsLoadContext> instance = new ThreadLocal<>();

    public static OsLoadContext get() {
        if (instance.get() == null) throw new IllegalStateException("OsLoadContext not initialized");

        return instance.get();
    }

    public static void inContext(OsLoadContext context, Runnable runnable) {
        instance.set(context);
        runnable.run();
        instance.remove();
    }
}
