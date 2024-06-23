package com.ultreon.devices.core.print.task;

import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.block.entity.PrinterBlockEntity;
import com.ultreon.devices.core.network.NetworkDevice;
import com.ultreon.devices.core.network.Router;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

import java.util.UUID;

/**
 * @author MrCrayfish
 */
public class TaskPrint extends Task {
    private BlockPos devicePos;
    private UUID printerId;
    private IPrint print;

    public TaskPrint() {
        super("print");
    }

    public TaskPrint(BlockPos devicePos, NetworkDevice printer, IPrint print) {
        this();
        this.devicePos = devicePos;
        this.printerId = printer.getId();
        this.print = print;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putLong("devicePos", devicePos.asLong());
        tag.putUUID("printerId", printerId);
        tag.put("print", IPrint.save(print));
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        TileEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("devicePos"))).getBlockEntity(BlockPos.of(tag.getLong("devicePos")), Chunk.CreateEntityType.IMMEDIATE);
        if (tileEntity instanceof NetworkDeviceBlockEntity device) {
            Router router = device.getRouter();
            if (router != null) {
                NetworkDeviceBlockEntity printer = router.getDevice(level, tag.getUUID("printerId"));
                if (printer instanceof PrinterBlockEntity) {
                    IPrint print = IPrint.load(tag.getCompound("print"));
                    ((PrinterBlockEntity) printer).addToQueue(print);
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
