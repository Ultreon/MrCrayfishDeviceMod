package com.ultreon.devices.core.network.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.core.network.Router;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

/**
 * @author MrCrayfish
 */
public class TaskConnect extends Task {
    private BlockPos devicePos;
    private BlockPos routerPos;

    public TaskConnect() {
        super("connect");
    }

    public TaskConnect(BlockPos devicePos, BlockPos routerPos) {
        this();
        this.devicePos = devicePos;
        this.routerPos = routerPos;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putLong("devicePos", devicePos.asLong());
        tag.putLong("routerPos", routerPos.asLong());
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        TileEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("routerPos"))).getBlockEntity(BlockPos.of(tag.getLong("routerPos")), Chunk.CreateEntityType.IMMEDIATE);
        if (tileEntity instanceof RouterBlockEntity tileEntityRouter) {
            Router router = tileEntityRouter.getRouter();

            TileEntity tileEntity1 = level.getChunkAt(BlockPos.of(tag.getLong("devicePos"))).getBlockEntity(BlockPos.of(tag.getLong("devicePos")), Chunk.CreateEntityType.IMMEDIATE);
            if (tileEntity1 instanceof NetworkDeviceBlockEntity tileEntityNetworkDevice) {
                if (router.addDevice(tileEntityNetworkDevice)) {
                    tileEntityNetworkDevice.connect(router);
                    this.setSuccessful();
                }
            }
        }
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {

    }

    @Override
    public void processResponse(CompoundNBT tag) {

    }
}
