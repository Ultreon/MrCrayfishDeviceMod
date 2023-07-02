package com.ultreon.devices.block.entity;

import com.ultreon.devices.init.DeviceBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LaptopBlockEntity extends ComputerBlockEntity {
    public LaptopBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.LAPTOP.get(), pWorldPosition, pBlockState);
    }
}
