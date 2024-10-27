package com.ultreon.devices.tests;

import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.mods.lib.client.gui.screen.test.TestLaunchContext;
import com.ultreon.mods.lib.client.gui.screen.test.TestScreen;
import com.ultreon.mods.lib.client.gui.screen.test.TestScreenInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;

@TestScreenInfo("DV TEST")
public class DvTest1 extends Screen implements TestScreen {
    public DvTest1() {
        super(TestLaunchContext.get().title);
    }

    @Override
    protected void init() {
        Minecraft.getInstance().setScreen(new Laptop(new LaptopBlockEntity(new BlockPos(0, 0, 0), DeviceBlocks.LAPTOPS.of(DyeColor.WHITE).get().defaultBlockState()), true));
    }
}
