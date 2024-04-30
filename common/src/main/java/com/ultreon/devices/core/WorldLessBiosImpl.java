package com.ultreon.devices.core;

import com.ultreon.devices.api.bios.Bios;
import com.ultreon.devices.api.bios.BiosNotification;
import com.ultreon.devices.api.bios.PowerModeInterrupt;
import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.os.OperatingSystem;
import com.ultreon.devices.core.client.ClientNotification;

import java.util.UUID;

public class WorldLessBiosImpl implements Bios {
    private static final UUID VIRTUAL_ID = new UUID(0xaa7bcf606c8c4f13L, 0x8f2fc2359c6b2543L);
    private OperatingSystem runningOS;
    private final BootLoader<?> os;

    public WorldLessBiosImpl(BootLoader<?> os) {
        this.os = os;
    }

    @Override
    public void sendNotification(BiosNotification notification) {
        ClientNotification.of(notification).push();
    }

    public boolean powerOn() {
        runningOS = this.os.start(null, this);
        return runningOS != null;
    }

    @Override
    public boolean powerOff() {
        return runningOS.onBiosInterrupt(new PowerModeInterrupt(PowerModeInterrupt.PowerMode.SHUTDOWN));
    }

    @Override
    public void addOperatingSystem(BootLoader<?> operatingSystem) {
        // No-op
    }

    @Override
    public OperatingSystem getRunningOS() {
        return null;
    }

    @Override
    public Drive getMainDrive() {
        return null;
    }

    @Override
    public void setMainDrive(Drive drive) {

    }

    @Override
    public UUID getDeviceId() {
        return VIRTUAL_ID;
    }
}
