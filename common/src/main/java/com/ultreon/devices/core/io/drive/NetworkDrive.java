package com.ultreon.devices.core.io.drive;

import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.ServerFolder;
import com.ultreon.devices.core.io.action.FileAction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

/// @author MrCrayfish
public final class NetworkDrive extends AbstractDrive {
    private final BlockPos pos;

    public NetworkDrive(String name, BlockPos pos) {
        super(name);
        this.pos = pos;
    }

    @Nullable
    @Override
    @Deprecated
    public ServerFolder getRoot(Level level) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof Interface impl) {
            AbstractDrive drive = impl.getDrive();
            if (drive != null) {
                return drive.getRoot(level);
            }
        }
        return null;
    }

    @Override
    public FileSystem.Response handleFileAction(FileSystem fileSystem, FileAction action, Level level) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof Interface impl) {
            AbstractDrive drive = impl.getDrive();
            if (drive.handleFileAction(fileSystem, action, level).getStatus() == FileSystem.Status.SUCCESSFUL) {
                tileEntity.setChanged();
                return FileSystem.createSuccessResponse();
            }
        }
        return FileSystem.createResponse(FileSystem.Status.DRIVE_UNAVAILABLE, "The network drive could not be found");
    }

    @Nullable
    @Override
    @Deprecated
    public ServerFolder getFolder(String path) {
        return null;
    }

    @Override
    public Type getType() {
        return Type.NETWORK;
    }

    @Override
    public CompoundTag toTag() {
        return null;
    }

    public interface Interface {
        AbstractDrive getDrive();

        boolean canAccessDrive();
    }
}
