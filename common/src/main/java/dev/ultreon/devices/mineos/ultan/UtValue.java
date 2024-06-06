package dev.ultreon.devices.mineos.ultan;

import org.jetbrains.annotations.Nullable;

public interface UtValue {
    @Nullable Object get();

    void set(@Nullable Object o) throws UltanException;

    UtValue copy();
}
