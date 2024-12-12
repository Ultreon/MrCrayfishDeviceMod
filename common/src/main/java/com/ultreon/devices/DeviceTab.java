package com.ultreon.devices;

import com.ultreon.devices.init.DeviceItems;
import dev.ultreon.mods.xinexlib.platform.Services;
import dev.ultreon.mods.xinexlib.platform.services.IRegistrar;
import dev.ultreon.mods.xinexlib.platform.services.IRegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class DeviceTab {
    @SuppressWarnings("UnstableApiUsage")
    public static IRegistrySupplier<CreativeModeTab, CreativeModeTab> create() {
        Devices.LOGGER.info("Creating Creative Tab...");
        IRegistrar<CreativeModeTab> creativeModeTabIRegistrar = Devices.REGISTRIES.get().getRegistrar(Registries.CREATIVE_MODE_TAB);

        creativeModeTabIRegistrar.register("tab", () -> Services.creativeTabBuilder().title(Component.literal("Ultreon Devices Mod")).icon(() -> new ItemStack(DeviceItems.LAPTOPS.of(DyeColor.RED))).build());
        return devicesTabDevice;
    }
}
