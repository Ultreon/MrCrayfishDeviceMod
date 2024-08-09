package dev.ultreon.devices.core;

import dev.ultreon.devices.UltreonDevicesMod;
import dev.ultreon.vbios.Bios;
import dev.ultreon.devices.api.os.OperatingSystem;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.architectury.extensions.injected.InjectedRegistryEntryExtension;
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
