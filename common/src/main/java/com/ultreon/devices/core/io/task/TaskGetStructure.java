package com.ultreon.devices.core.io.task;

import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.ServerFolder;
import com.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

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
    public void prepareRequest(CompoundTag tag) {
        tag.putString("uuid", uuid);
        tag.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockEntity tileEntity = level.getBlockEntity(BlockPos.of(tag.getLong("pos")));
        if (tileEntity instanceof LaptopBlockEntity laptop) {
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
    public void prepareResponse(CompoundTag tag) {
        if (folder != null) {
            tag.putString("file_name", folder.getName());
            tag.put("structure", folder.toTag());
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
