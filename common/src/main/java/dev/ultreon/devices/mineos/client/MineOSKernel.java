package dev.ultreon.devices.mineos.client;

import dev.ultreon.devices.api.bios.Bios;
import dev.ultreon.devices.mineos.DriverManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;

public class MineOSKernel {
    private static final DriverManager driverManager = new DriverManager();

    public void boot(MineOS mineOS, Bios bios) {
        this.loadDrivers(mineOS, bios);
    }

    private void loadDrivers(MineOS mineOS, Bios bios) {

    }

    public void playSound(SoundEvent sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1f, 1f));
    }
}
