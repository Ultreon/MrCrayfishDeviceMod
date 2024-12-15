package com.ultreon.devices.block;

import com.ultreon.devices.block.entity.ComputerBlockEntity;
import com.ultreon.devices.core.ComputerScreen;
import net.minecraft.client.Minecraft;

public class ClientLaptopWrapper {

    public static void execute(ComputerBlockEntity laptop) {
        Minecraft.getInstance().setScreen(new ComputerScreen(laptop));
    }
}
