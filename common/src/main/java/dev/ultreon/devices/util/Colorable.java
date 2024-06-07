package dev.ultreon.devices.util;

import net.minecraft.world.item.DyeColor;

public interface Colorable extends Colored {
    @Override
    DyeColor getColor();

    void setColor(DyeColor color);
}
