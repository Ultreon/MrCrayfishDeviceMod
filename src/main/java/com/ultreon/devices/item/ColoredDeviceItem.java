package com.ultreon.devices.item;

import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.util.Colored;
import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import org.jetbrains.annotations.NotNull;

public class ColoredDeviceItem extends DeviceItem implements Colored {
    private final DyeColor color;

    public ColoredDeviceItem(@NotNull Block block, Properties tab, DyeColor color, ModDeviceTypes deviceType) {
        super(block, tab, deviceType);
        this.color = color;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }
}
