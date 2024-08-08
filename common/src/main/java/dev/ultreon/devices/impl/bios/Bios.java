package dev.ultreon.devices.impl.bios;

import dev.ultreon.devices.api.device.Drive;
import dev.ultreon.devices.api.os.OperatingSystem;
import dev.ultreon.devices.core.BootLoader;

import java.util.UUID;

public interface Bios {
    void sendNotification(BiosNotification notification);

    boolean powerOff();

    void addOperatingSystem(BootLoader<?> operatingSystem);

    OperatingSystem getRunningOS();

    Drive getMainDrive();

    void setMainDrive(Drive drive);

    UUID getDeviceId();
}
