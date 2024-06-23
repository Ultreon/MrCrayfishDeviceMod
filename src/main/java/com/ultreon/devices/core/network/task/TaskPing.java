package com.ultreon.devices.core.network.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * @author MrCrayfish
 */
public class TaskPing extends Task {
    private BlockPos sourceDevicePos;
    private int strength;

    public TaskPing() {
        super("ping");
    }

    public TaskPing(BlockPos sourceDevicePos) {
        this();
        this.sourceDevicePos = sourceDevicePos;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putLong("sourceDevicePos", sourceDevicePos.asLong());
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        TileEntity blockEntity = level.getChunkAt(BlockPos.of(tag.getLong("sourceDevicePos"))).getBlockEntity(BlockPos.of(tag.getLong("sourceDevicePos")), Chunk.CreateEntityType.IMMEDIATE);
        if (blockEntity instanceof NetworkDeviceBlockEntity) {
            NetworkDeviceBlockEntity networkDevice = (NetworkDeviceBlockEntity) blockEntity;
            if (networkDevice.isConnected()) {
                this.strength = networkDevice.getSignalStrength();
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {
        if (this.isSucessful()) {
            tag.putInt("strength", strength);
        }
    }

    @Override
    public void processResponse(CompoundNBT tag) {

    }
}
