package com.ultreon.devices.util;

import net.minecraft.item.DyeColor;

public interface Colorable extends Colored {
    @Override
    DyeColor getColor();

    void setColor(DyeColor color);
}
