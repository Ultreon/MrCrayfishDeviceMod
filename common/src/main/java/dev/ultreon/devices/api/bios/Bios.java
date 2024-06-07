package dev.ultreon.devices.api.bios;

import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.api.os.OperatingSystem;
import dev.ultreon.devices.core.BootLoader;
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
