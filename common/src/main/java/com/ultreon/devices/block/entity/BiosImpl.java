package com.ultreon.devices.block.entity;

import com.ultreon.devices.core.Bios;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.PowerMode;
import net.minecraft.client.Minecraft;

public class BiosImpl implements Bios {
    private final ComputerBlockEntity blockEntity;

    public BiosImpl(ComputerBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public void systemExit(PowerMode state) {
        if (state == PowerMode.SHUTDOWN) {
            if (blockEntity.isOpen()) {
                blockEntity.openClose(null);
                Minecraft.getInstance().setScreen(null);
            }
        } else if (!blockEntity.isOpen()) {
            Minecraft.getInstance().setScreen(new Laptop(blockEntity));
        }
    }
}
