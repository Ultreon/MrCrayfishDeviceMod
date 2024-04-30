package com.ultreon.devices.item;

import com.ultreon.devices.UltreonDevicesMod;
import net.minecraft.world.item.Item;

public class ComponentItem extends Item {
    public ComponentItem(Properties pProperties) {
        super(pProperties.arch$tab(UltreonDevicesMod.TAB_DEVICE));
    }
}
