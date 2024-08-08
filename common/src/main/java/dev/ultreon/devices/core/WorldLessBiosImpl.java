package dev.ultreon.devices.core;

import dev.ultreon.devices.api.bios.Bios;
import dev.ultreon.devices.api.bios.BiosCallType;
import dev.ultreon.devices.api.bios.BiosInterruptType;
import dev.ultreon.devices.api.bios.InterruptHandler;
import dev.ultreon.devices.impl.bios.BiosNotification;
import dev.ultreon.devices.impl.bios.PowerModeInterrupt;
import dev.ultreon.devices.impl.io.Drive;
import dev.ultreon.devices.api.os.OperatingSystem;
import dev.ultreon.devices.core.client.ClientNotification;

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

    @Override
    public void registerInterrupt(BiosInterruptType interrupt, InterruptHandler handler) {

    }

    @Override
    public void enableInterrupt(BiosInterruptType interrupt) {

    }

    @Override
    public void disableInterrupt(BiosInterruptType interrupt) {

    }

    @Override
    public Object call(BiosCallType call, Object[] args) {
        switch (call) {
            case POWER_OFF -> {
                return powerOff();
            }
            case GET_RUNNING_OS -> {
                return getRunningOS().getDeviceId();
            }
            default -> {
                return null;
            }
        }
    }
}
