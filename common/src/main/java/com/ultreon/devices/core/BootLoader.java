package com.ultreon.devices.core;

import com.ultreon.devices.UltreonDevicesMod;
import com.ultreon.devices.api.bios.Bios;
import com.ultreon.devices.api.os.OperatingSystem;
import com.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.architectury.extensions.injected.InjectedRegistryEntryExtension;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface BootLoader<T extends OperatingSystem> extends InjectedRegistryEntryExtension<BootLoader<?>> {
    T start(ComputerBlockEntity computer, Bios bios);

    @Override
    default Holder<BootLoader<?>> arch$holder() {
        return UltreonDevicesMod.OPERATING_SYSTEM.getHolder(arch$registryName());
    }

    @Override
    default @Nullable ResourceLocation arch$registryName() {
        return UltreonDevicesMod.OPERATING_SYSTEM.getId(this);
    }
}
