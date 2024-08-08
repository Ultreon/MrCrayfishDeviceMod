package dev.ultreon.devices.impl.device;

import dev.ultreon.devices.api.device.HardwareDevice;
import dev.ultreon.devices.impl.bios.DeviceInfo;
import dev.ultreon.devices.impl.bios.DisplayDevice;
import dev.ultreon.devices.impl.bios.VEFI_SystemImpl;

import java.nio.ByteBuffer;
import java.util.UUID;

public interface HostDevice {
    void powerOff();

    PhysicalHardwareDevice getPhysicalDevice(UUID id);

    DeviceInfo getDevices();

    ByteBuffer getBiosData();

    void enterSleep();

    void exitSleep();

    DisplayDevice getGraphics();
}
