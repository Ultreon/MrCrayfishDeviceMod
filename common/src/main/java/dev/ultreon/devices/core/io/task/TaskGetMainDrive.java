package dev.ultreon.devices.core.io.task;

import dev.ultreon.devices.api.bios.Bios;
import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.api.io.Folder;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.core.io.drive.AbstractDrive;
import dev.ultreon.devices.mineos.client.MineOS;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * @author MrCrayfish
 */
public class TaskGetMainDrive extends Task {
    private BlockPos pos;

    private AbstractDrive mainDrive;

    public TaskGetMainDrive() {
        super("get_main_drive");
    }

    public TaskGetMainDrive(BlockPos pos) {
        this();
        this.pos = pos;
    }

    @Override
    public void prepareRequest(CompoundTag tag) {
        tag.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        BlockEntity tileEntity = level.getBlockEntity(BlockPos.of(tag.getLong("pos")));
        if (tileEntity instanceof ComputerBlockEntity laptop) {
            FileSystem fileSystem = laptop.getFileSystem();
            mainDrive = fileSystem.getMainDrive();
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (this.isSuccessful()) {
            CompoundTag mainDriveTag = new CompoundTag();
            mainDriveTag.putString("name", mainDrive.getName());
            mainDriveTag.putString("uuid", mainDrive.getUuid().toString());
            mainDriveTag.putString("type", mainDrive.getType().toString());
            tag.put("main_drive", mainDriveTag);
            tag.put("structure", mainDrive.getDriveStructure().toTag());
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {
        if (this.isSuccessful()) {
            if (Minecraft.getInstance().screen instanceof MineOS) {
                CompoundTag structureTag = tag.getCompound("structure");
                Drive drive = new Drive(tag.getCompound("main_drive"));
                drive.syncRoot(Folder.fromTag(FileSystem.LAPTOP_DRIVE_NAME, structureTag));
                drive.getRoot().validate();

                Bios bios = MineOS.getOpened().getBios();
                if (bios.getMainDrive() == null) {
                    bios.setMainDrive(drive);
                }
            }
        }
    }
}
