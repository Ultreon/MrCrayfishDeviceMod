package com.ultreon.devices.programs.system.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TaskUpdateApplicationData extends Task {
    private int x, y, z;
    private String appId;
    private CompoundNBT data;

    public TaskUpdateApplicationData() {
        super("update_application_data");
    }

    public TaskUpdateApplicationData(int x, int y, int z, @Nonnull String appId, @Nonnull CompoundNBT data) {
        this();
        this.x = x;
        this.y = y;
        this.z = z;
        this.appId = appId;
        this.data = data;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putInt("posX", this.x);
        tag.putInt("posY", this.y);
        tag.putInt("posZ", this.z);
        tag.putString("appId", this.appId);
        tag.put("appData", this.data);
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        TileEntity tileEntity = level.getBlockEntity(new BlockPos(tag.getInt("posX"), tag.getInt("posY"), tag.getInt("posZ")));
        if (tileEntity instanceof LaptopBlockEntity) {
            LaptopBlockEntity laptop = (LaptopBlockEntity) tileEntity;
            laptop.setApplicationData(tag.getString("appId"), tag.getCompound("appData"));
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
