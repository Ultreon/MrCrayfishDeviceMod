package com.ultreon.devices.item;

import com.ultreon.devices.Devices;
import net.minecraft.item.Item;

public class ComponentItem extends Item {
    public ComponentItem(Properties pProperties) {
        super(pProperties.tab(Devices.GROUP_DEVICE));
    }
}
