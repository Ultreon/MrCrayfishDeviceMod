package com.ultreon.devices.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;

public class BlockEntityUtil {
    public static void markBlockForUpdate(World level, BlockPos pos) {
        level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
    }

    public static <T extends TileEntity> BlockEntityTicker<T> getTicker() {
        return (pLevel, pPos, pState, pBlockEntity) -> {
            if (pBlockEntity instanceof Tickable) {
                ((Tickable) pBlockEntity).tick();
            }
        };
    }

    public static void setBlockState(World level, BlockPos pos, BlockState state, int flags) {
        if (level instanceof ServerWorld serverLevel) {
            serverLevel.setBlock(pos, state, flags);
        }
    }

}
