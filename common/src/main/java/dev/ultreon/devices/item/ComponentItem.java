package dev.ultreon.devices.item;

import dev.ultreon.devices.UltreonDevicesMod;
import net.minecraft.world.item.Item;

public class ComponentItem extends Item {
    public ComponentItem(Properties pProperties) {
        super(pProperties.arch$tab(UltreonDevicesMod.TAB_DEVICE));
    }
}
