package com.ultreon.devices.programs.system.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class TaskUpdateSystemData extends Task {
    private BlockPos pos;
    private CompoundNBT data;

    public TaskUpdateSystemData() {
        super("update_system_data");
    }

    public TaskUpdateSystemData(BlockPos pos, CompoundNBT data) {
        this();
        this.pos = pos;
        this.data = data;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putLong("pos", pos.asLong());
        tag.put("data", this.data);
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        BlockPos pos = BlockPos.of(tag.getLong("pos"));
        TileEntity tileEntity = level.getChunkAt(pos).getBlockEntity(pos, Chunk.CreateEntityType.IMMEDIATE);
        if (tileEntity instanceof LaptopBlockEntity) {
            LaptopBlockEntity laptop = (LaptopBlockEntity) tileEntity;
            laptop.setSystemData(tag.getCompound("data"));
        }
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {

    }

    @Override
    public void processResponse(CompoundNBT tag) {

    }
}
