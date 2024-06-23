package com.ultreon.devices.core.network.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.core.network.NetworkDevice;
import com.ultreon.devices.core.network.Router;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Collection;

/**
 * @author MrCrayfish
 */
public class TaskGetDevices extends Task {
    private BlockPos devicePos;
    private Class<? extends NetworkDeviceBlockEntity> targetDeviceClass;

    private Collection<NetworkDevice> foundDevices;

    public TaskGetDevices() {
        super("get_network_devices");
    }

    public TaskGetDevices(BlockPos devicePos) {
        this();
        this.devicePos = devicePos;
    }

    public TaskGetDevices(BlockPos devicePos, Class<? extends NetworkDeviceBlockEntity> targetDeviceClass) {
        this();
        this.devicePos = devicePos;
        this.targetDeviceClass = targetDeviceClass;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putLong("devicePos", devicePos.asLong());
        if (targetDeviceClass != null) {
            tag.putString("targetClass", targetDeviceClass.getName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        BlockPos devicePos = BlockPos.of(tag.getLong("devicePos"));
        Class<? extends NetworkDeviceBlockEntity> targetDeviceClass = null;
        try {
            Class<?> targetClass = Class.forName(tag.getString("targetClass"));
            if (NetworkDeviceBlockEntity.class.isAssignableFrom(targetClass)) {
                targetDeviceClass = (Class<? extends NetworkDeviceBlockEntity>) targetClass;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        TileEntity tileEntity = level.getChunkAt(devicePos).getBlockEntity(devicePos, Chunk.CreateEntityType.IMMEDIATE);
        if (tileEntity instanceof NetworkDeviceBlockEntity) {
            NetworkDeviceBlockEntity tileEntityNetworkDevice = (NetworkDeviceBlockEntity) tileEntity;
            if (tileEntityNetworkDevice.isConnected()) {
                Router router = tileEntityNetworkDevice.getRouter();
                if (router != null) {
                    if (targetDeviceClass != null) {
                        foundDevices = router.getConnectedDevices(level, targetDeviceClass);
                    } else {
                        foundDevices = router.getConnectedDevices(level);
                    }
                    this.setSuccessful();
                }
            }
        }
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {
        if (this.isSucessful()) {
            ListNBT deviceList = new ListNBT();
            foundDevices.forEach(device -> deviceList.add(device.toTag(true)));
            tag.put("network_devices", deviceList);
        }
    }

    @Override
    public void processResponse(CompoundNBT tag) {

    }
}
