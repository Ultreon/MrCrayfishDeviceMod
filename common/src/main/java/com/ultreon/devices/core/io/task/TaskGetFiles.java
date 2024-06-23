package com.ultreon.devices.core.io.task;

import com.ultreon.devices.api.io.Folder;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.ServerFile;
import com.ultreon.devices.core.io.ServerFolder;
import com.ultreon.devices.core.io.drive.AbstractDrive;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author MrCrayfish
 */
public class TaskGetFiles extends Task {
    private String uuid;
    private String path;
    private BlockPos pos;

    private List<ServerFile> files;

    public TaskGetFiles() {
        super("get_files");
    }

    public TaskGetFiles(Folder folder, BlockPos pos) {
        this();
        this.uuid = folder.getDrive().getUUID().toString();
        this.path = folder.getPath();
        this.pos = pos;
    }

    protected static String compileDirectory(ServerFile file) {
        if (file.getParent() == null || file.getParent().getParent() == null) return "/";

        StringBuilder builder = new StringBuilder();
        ServerFolder parent = file.getParent();
        while (parent != null) {
            builder.insert(0, "/" + parent.getName());
            if (parent.getParent() != null) {
                return builder.toString();
            }
            parent = parent.getParent();
        }
        return builder.toString();
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putString("uuid", uuid);
        tag.putString("path", path);
        tag.putLong("pos", pos.asLong());
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        TileEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos"))).getBlockEntity(BlockPos.of(tag.getLong("pos")), Chunk.CreateEntityType.IMMEDIATE);
        if (tileEntity instanceof LaptopBlockEntity laptop) {
            FileSystem fileSystem = laptop.getFileSystem();
            UUID uuid = UUID.fromString(tag.getString("uuid"));
            AbstractDrive serverDrive = fileSystem.getAvailableDrives(level, true).get(uuid);
            if (serverDrive != null) {
                ServerFolder found = serverDrive.getFolder(tag.getString("path"));
                if (found != null) {
                    this.files = found.getFiles().stream().filter(f -> !f.isFolder()).collect(Collectors.toList());
                    this.setSuccessful();
                }
            }
        }
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {
        if (this.files != null) {
            ListNBT list = new ListNBT();
            this.files.forEach(f -> {
                CompoundNBT fileTag = new CompoundNBT();
                fileTag.putString("file_name", f.getName());
                fileTag.put("data", f.toTag());
                list.add(fileTag);
            });
            tag.put("files", list);
        }
    }

    @Override
    public void processResponse(CompoundNBT tag) {

    }
}
