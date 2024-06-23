package com.ultreon.devices.block;

import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;

public class ClientLaptopWrapper {

    public static void execute(LaptopBlockEntity laptop) {
        Minecraft.getInstance().setScreen(new Laptop(laptop));
    }
}
