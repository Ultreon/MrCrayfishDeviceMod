package dev.ultreon.devices.core;

import dev.ultreon.devices.UltreonDevicesMod;
import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.core.client.ClientNotification;
import dev.ultreon.devices.api.bios.*;
import dev.ultreon.devices.api.os.OperatingSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.UUID;

public class BiosImpl implements Bios {
    private final ComputerBlockEntity computer;
    private final List<BootLoader<?>> bootLoaders;
    private OperatingSystem runningOS;
    private Drive mainDrive;

    public BiosImpl(ComputerBlockEntity computer, List<BootLoader<?>> bootLoaders) {
        this.computer = computer;
        this.bootLoaders = bootLoaders;
    }

    @Override
    public void sendNotification(BiosNotification notification) {
        ClientNotification.of(notification).push();
    }

    public void powerOn() {
        for (BootLoader<?> bootLoader : bootLoaders) {
            try {
                this.runningOS = bootLoader.start(computer, this);
                if (this.runningOS != null) return;
            } catch (Exception e) {
                UltreonDevicesMod.LOGGER.error("Failed to boot", e);

                this.handleFault(e);
                return;
            }
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.displayClientMessage(Component.literal("Failed to boot, no operating system found!"), true);
        }
    }

    @Override
    public boolean powerOff() {
        if (this.runningOS != null) {
            try {
                if (this.runningOS.onBiosInterrupt(new PowerModeInterrupt(PowerModeInterrupt.PowerMode.SHUTDOWN))) {
                    return false;
                }
            } catch (Exception e) {
                UltreonDevicesMod.LOGGER.error("Failed to shutdown", e);

                this.handleFault(e);
            }
        }
        return true;
    }

    @Override
    public void addOperatingSystem(BootLoader<?> operatingSystem) {
        this.bootLoaders.add(operatingSystem);
    }

    @Override
    public OperatingSystem getRunningOS() {
        return this.runningOS;
    }

    @Override
    public Drive getMainDrive() {
        return mainDrive;
    }

    @Override
    public void setMainDrive(Drive drive) {
        this.mainDrive = drive;
    }

    @Override
    public UUID getDeviceId() {
        return computer.getId();
    }

    private void handleFault(Exception e) {
        try {
            this.runningOS.onBiosInterrupt(new FaultInterrupt(e, "An error occurred in the BIOS"));
        } catch (Exception ex) {
            UltreonDevicesMod.LOGGER.error("Failed to handle fault", ex);

            try {
                this.runningOS.onBiosInterrupt(new DoubleFaultInterrupt(ex, List.of(e), "Failed to handle fault"));
            } catch (Exception exc) {
                UltreonDevicesMod.LOGGER.error("Failed to handle double fault", exc);

                this.runningOS = null;
            }
        }
    }

    public void onFault(Exception e) {
        this.handleFault(e);
    }
}
