package com.ultreon.devices.core.io.task;

import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.action.FileAction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/**
 * @author MrCrayfish
 */
public class TaskSendAction extends Task {
    private String uuid;
    private FileAction action;
    private BlockPos pos;

    private FileSystem.Response response;

    public TaskSendAction() {
        super("send_action");
    }

    public TaskSendAction(Drive drive, FileAction action) {
        this();
        this.uuid = drive.getUUID().toString();
        this.action = action;
        this.pos = Laptop.getPos();
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putString("uuid", uuid);
        tag.put("action", action.toTag());
        tag.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        FileAction action = FileAction.fromTag(tag.getCompound("action"));
        TileEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos"))).getBlockEntity(BlockPos.of(tag.getLong("pos")), Chunk.CreateEntityType.IMMEDIATE);
        if (tileEntity instanceof LaptopBlockEntity) {
            LaptopBlockEntity laptop = (LaptopBlockEntity) tileEntity;
            response = laptop.getFileSystem().readAction(tag.getString("uuid"), action, level);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {
        tag.put("response", response.toTag());
    }

    @Override
    public void processResponse(CompoundNBT tag) {

    }
}
