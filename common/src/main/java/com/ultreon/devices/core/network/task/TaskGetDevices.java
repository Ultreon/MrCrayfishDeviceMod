package com.ultreon.devices.core.network.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.core.network.NetworkDevice;
import com.ultreon.devices.core.network.Router;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

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
    public void prepareRequest(CompoundTag tag) {
        tag.putLong("devicePos", devicePos.asLong());
        if (targetDeviceClass != null) {
            tag.putString("targetClass", targetDeviceClass.getName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processRequest(CompoundTag tag, Level level, Player player) {
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

        BlockEntity tileEntity = level.getChunkAt(devicePos).getBlockEntity(devicePos, LevelChunk.EntityCreationType.IMMEDIATE);
        if (tileEntity instanceof NetworkDeviceBlockEntity tileEntityNetworkDevice) {
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
    public void prepareResponse(CompoundTag tag) {
        if (this.isSucessful()) {
            ListTag deviceList = new ListTag();
            foundDevices.forEach(device -> deviceList.add(device.toTag(true)));
            tag.put("network_devices", deviceList);
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
