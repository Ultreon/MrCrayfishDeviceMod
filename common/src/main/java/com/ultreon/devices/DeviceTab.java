package com.ultreon.devices;

import com.ultreon.devices.init.DeviceItems;
import dev.ultreon.mods.xinexlib.platform.Services;
import dev.ultreon.mods.xinexlib.registrar.Registrar;
import dev.ultreon.mods.xinexlib.registrar.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class DeviceTab {
    @SuppressWarnings("UnstableApiUsage")
    public static RegistrySupplier<CreativeModeTab, CreativeModeTab> create() {
        Devices.LOGGER.info("Creating Creative Tab...");
        Registrar<CreativeModeTab> creativeModeTabRegistrar = Devices.REGISTRIES.get().getRegistrar(Registries.CREATIVE_MODE_TAB);

        RegistrySupplier<CreativeModeTab, CreativeModeTab> register = creativeModeTabRegistrar.register("tab", () -> Services.creativeTabBuilder().title(Component.literal("Ultreon Devices Mod")).icon(() -> new ItemStack(DeviceItems.LAPTOPS.of(DyeColor.RED))).build());

        return register;
    }
}
