package com.ultreon.devices.api.bios;

import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.os.OperatingSystem;
import com.ultreon.devices.core.BootLoader;
import net.minecraft.client.gui.screens.Screen;

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
