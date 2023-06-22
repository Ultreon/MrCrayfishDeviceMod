package com.ultreon.devices;

import com.ultreon.devices.init.DeviceItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DeviceTab extends CreativeModeTab {
    public DeviceTab(String label) {
        super(0, "A");
        throw new AssertionError();
    }

    @NotNull
    @Override
    public ItemStack makeIcon() {
        return new ItemStack(DeviceItems.LAPTOPS.of(DyeColor.RED).get());
    }
}
