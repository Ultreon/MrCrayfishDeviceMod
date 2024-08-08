package dev.ultreon.devices.impl.device;

import dev.ultreon.devices.api.device.HardwareDevice;

public interface PhysicalHardwareDevice extends HardwareDevice {
    @Override
    default boolean isPhysical() {
        return true;
    }
}
