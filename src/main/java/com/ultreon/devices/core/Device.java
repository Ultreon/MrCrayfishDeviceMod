package com.ultreon.devices.core;

import com.ultreon.devices.block.entity.DeviceBlockEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Device {
    protected UUID id;
    protected String name;
    protected BlockPos pos;

    protected Device() {

    }

    public Device(@NotNull DeviceBlockEntity device) {
        this.id = device.getId();
        update(device);
    }

    public Device(@NotNull UUID id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public BlockPos getPos() {
        return pos;
    }

    public void setPos(@Nullable BlockPos pos) {
        this.pos = pos;
    }

    public void update(@NotNull DeviceBlockEntity device) {
        name = device.getCustomName();
        pos = device.getBlockPos();
    }

    @Nullable
    public DeviceBlockEntity getDevice(@NotNull World level) {
        if (pos == null)
            return null;

        TileEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DeviceBlockEntity) {
            DeviceBlockEntity deviceBlockEntity = (DeviceBlockEntity) blockEntity;
            if (deviceBlockEntity.getId().equals(getId())) {
                return deviceBlockEntity;
            }
        }

        return null;
    }

    public CompoundNBT toTag(boolean includePos) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("id", getId().toString());
        tag.putString("name", getName());
        if (includePos) {
            tag.putLong("pos", pos.asLong());
        }
        return tag;
    }

    public static Device fromTag(CompoundNBT tag) {
        Device device = new Device();
        device.id = UUID.fromString(tag.getString("id"));
        device.name = tag.getString("name");
        if (tag.contains("pos", Constants.NBT.TAG_LONG)) {
            device.pos = BlockPos.of(tag.getLong("pos"));
        }
        return device;
    }
}
