package com.ultreon.devices.item;

import com.ultreon.devices.Devices;
import net.minecraft.item.Item;

/**
 * @author MrCrayfish
 */
public class BasicItem extends Item {
    public BasicItem(Properties pProperties) {
        super(pProperties.tab(Devices.GROUP_DEVICE));
    }
}
