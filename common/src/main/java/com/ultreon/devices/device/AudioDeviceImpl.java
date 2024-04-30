package com.ultreon.devices.device;

import com.ultreon.devices.api.device.AudioDevice;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import static net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI;

public class AudioDeviceImpl implements AudioDevice {
    @Override
    public void playSound(String soundId) {
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(soundId));
        if (sound == null) return;

        Minecraft.getInstance().getSoundManager().play(forUI(sound, 1.0F, 1.0F));
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public boolean isConnected() {
        return true;
    }
}
