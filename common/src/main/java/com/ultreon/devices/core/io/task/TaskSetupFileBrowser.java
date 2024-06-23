package com.ultreon.devices.core.io.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

import java.util.Map;
import java.util.UUID;

/**
 * @author MrCrayfish
 */
public class TaskSetupFileBrowser extends Task {
    private BlockPos pos;
    private boolean includeMain;

    private AbstractDrive mainDrive;
    private Map<UUID, AbstractDrive> availableDrives;

    public TaskSetupFileBrowser() {
        super("get_file_system");
    }

    public TaskSetupFileBrowser(BlockPos pos, boolean includeMain) {
        this();
        this.pos = pos;
        this.includeMain = includeMain;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putLong("pos", pos.asLong());
        tag.putBoolean("include_main", includeMain);
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        TileEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos"))).getBlockEntity(BlockPos.of(tag.getLong("pos")), Chunk.CreateEntityType.IMMEDIATE);
        if (tileEntity instanceof LaptopBlockEntity laptop) {
            FileSystem fileSystem = laptop.getFileSystem();
            if (tag.getBoolean("include_main")) {
                mainDrive = fileSystem.getMainDrive();
            }
            availableDrives = fileSystem.getAvailableDrives(level, false);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {
        if (this.isSucessful()) {
            if (mainDrive != null) {
                CompoundNBT mainDriveTag = new CompoundNBT();
                mainDriveTag.putString("name", mainDrive.getName());
                mainDriveTag.putString("uuid", mainDrive.getUuid().toString());
                mainDriveTag.putString("type", mainDrive.getType().toString());
                tag.put("main_drive", mainDriveTag);
                tag.put("structure", mainDrive.getDriveStructure().toTag());
            }

            ListNBT driveList = new ListNBT();
            availableDrives.forEach((k, v) -> {
                CompoundNBT driveTag = new CompoundNBT();
                driveTag.putString("name", v.getName());
                driveTag.putString("uuid", v.getUuid().toString());
                driveTag.putString("type", v.getType().toString());
                driveList.add(driveTag);
            });
            tag.put("available_drives", driveList);
        }
    }

    @Override
    public void processResponse(CompoundNBT tag) {

    }
}
