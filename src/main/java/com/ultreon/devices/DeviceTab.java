package com.ultreon.devices;

import com.ultreon.devices.init.DeviceItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class DeviceTab extends ItemGroup {
    public DeviceTab(String label) {
        super(0, "A");
        throw new AssertionError();
    }

    @NotNull
    @Override
    public ItemStack makeIcon() {
        return new ItemStack(DeviceItems.RED_LAPTOP.get());
    }
}
