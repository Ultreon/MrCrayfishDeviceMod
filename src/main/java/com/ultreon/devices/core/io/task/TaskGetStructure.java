package com.ultreon.devices.core.io.task;

import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.ServerFolder;
import com.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * @author MrCrayfish
 */
public class TaskGetStructure extends Task {
    private String uuid;
    private BlockPos pos;

    private ServerFolder folder;

    public TaskGetStructure() {
        super("get_folder_structure");
    }

    public TaskGetStructure(Drive drive, BlockPos pos) {
        this();
        this.uuid = drive.getUUID().toString();
        this.pos = pos;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putString("uuid", uuid);
        tag.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        TileEntity tileEntity = level.getBlockEntity(BlockPos.of(tag.getLong("pos")));
        if (tileEntity instanceof LaptopBlockEntity) {
            LaptopBlockEntity laptop = (LaptopBlockEntity) tileEntity;
            FileSystem fileSystem = laptop.getFileSystem();
            UUID uuid = UUID.fromString(tag.getString("uuid"));
            AbstractDrive serverDrive = fileSystem.getAvailableDrives(level, true).get(uuid);
            if (serverDrive != null) {
                folder = serverDrive.getDriveStructure();
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {
        if (folder != null) {
            tag.putString("file_name", folder.getName());
            tag.put("structure", folder.toTag());
        }
    }

    @Override
    public void processResponse(CompoundNBT tag) {

    }
}
