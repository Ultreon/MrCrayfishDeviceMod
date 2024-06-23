package com.ultreon.devices.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BlockEntityUtil {
    public static void markBlockForUpdate(World level, BlockPos pos) {
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
    }

    public static void setBlockState(World level, BlockPos pos, BlockState state, int flags) {
        if (level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld) level;
            serverLevel.setBlock(pos, state, flags);
        }
    }

}
