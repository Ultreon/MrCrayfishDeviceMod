package com.ultreon.devices.block.entity;

import com.ultreon.devices.init.DeviceBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MacMaxXBlockEntity extends ComputerBlockEntity {
    public MacMaxXBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.MAC_MAX_X.get(), pWorldPosition, pBlockState);
    }
}
