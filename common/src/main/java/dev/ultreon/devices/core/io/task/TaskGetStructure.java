package dev.ultreon.devices.core.io.task;

import dev.ultreon.devices.UltreonDevicesMod;
import dev.ultreon.devices.impl.io.Drive;
import dev.ultreon.devices.impl.task.Task;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.ServerFolder;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
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
        BlockPos pos1 = BlockPos.of(tag.getLong("pos"));

        UltreonDevicesMod.getServer().submit(() -> {
            BlockEntity tileEntity = level.getBlockEntity(pos1);
            if (tileEntity instanceof ComputerBlockEntity laptop) {
                FileSystem fileSystem = laptop.getFileSystem();
                UUID uuid = UUID.fromString(tag.getString("uuid"));
                AbstractDrive serverDrive = fileSystem.getAvailableDrives(level, true).get(uuid);
                if (serverDrive != null) {
                    folder = serverDrive.getDriveStructure();
                    this.setSuccessful();
                }
            }
        }).join();
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
