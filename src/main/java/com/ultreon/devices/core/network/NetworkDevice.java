package com.ultreon.devices.core.network;

import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.core.Device;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class NetworkDevice extends Device {
    private NetworkDevice() {
        super();
    }

    public NetworkDevice(NetworkDeviceBlockEntity device) {
        super(device);
    }

    public NetworkDevice(@NotNull UUID id, @NotNull String name, @NotNull Router router) {
        super(id, name);
    }

    public boolean isConnected(World level) {
        if (pos == null) {
            return false;
        }

        TileEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof NetworkDeviceBlockEntity) {
            NetworkDeviceBlockEntity device = (NetworkDeviceBlockEntity) blockEntity;
            Router router = device.getRouter();
            return router != null && router.getId().equals(this.getId());
        }
        return false;
    }

    @Nullable
    @Override
    public NetworkDeviceBlockEntity getDevice(@NotNull World level) {
        if (pos == null)
            return null;

        TileEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof NetworkDeviceBlockEntity) {
            NetworkDeviceBlockEntity device = (NetworkDeviceBlockEntity) blockEntity;
            return device;
        }
        return null;
    }

    @Override
    public CompoundNBT toTag(boolean includePos) {
        CompoundNBT tag = super.toTag(includePos);
        if (includePos && pos != null) {
            tag.putLong("pos", pos.asLong());
        }
        return tag;
    }

    public static NetworkDevice fromTag(CompoundNBT tag) {
        NetworkDevice device = new NetworkDevice();
        device.id = UUID.fromString(tag.getString("id"));
        device.name = tag.getString("name");

        if (tag.contains("pos", Constants.NBT.TAG_LONG)) {
            device.pos = BlockPos.of(tag.getLong("pos"));
        }
        return device;
    }
}
