package dev.ultreon.devices.api.device;

import dev.ultreon.devices.api.IO;

public interface HardwareDevice {
    void sendSignal(int signal);

    IO io();

    default boolean isVirtual() {
        return !isPhysical();
    }

    boolean isPhysical();
}
