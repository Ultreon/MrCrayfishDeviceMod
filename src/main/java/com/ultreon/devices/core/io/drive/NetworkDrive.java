package com.ultreon.devices.core.io.drive;

import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.ServerFolder;
import com.ultreon.devices.core.io.action.FileAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author MrCrayfish
 */
public final class NetworkDrive extends AbstractDrive {
    private final BlockPos pos;

    public NetworkDrive(String name, BlockPos pos) {
        super(name);
        this.pos = pos;
        this.root = null;
    }

    @Nullable
    @Override
    public ServerFolder getRoot(World level) {
        TileEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof Interface) {
            Interface impl = (Interface) tileEntity;
            AbstractDrive drive = impl.getDrive();
            if (drive != null) {
                return drive.getRoot(level);
            }
        }
        return null;
    }

    @Override
    public FileSystem.Response handleFileAction(FileSystem fileSystem, FileAction action, World level) {
        TileEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof Interface) {
            Interface impl = (Interface) tileEntity;
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
    public ServerFolder getFolder(String path) {
        return null;
    }

    @Override
    public Type getType() {
        return Type.NETWORK;
    }

    @Override
    public CompoundNBT toTag() {
        return null;
    }

    public interface Interface {
        AbstractDrive getDrive();

        boolean canAccessDrive();
    }
}
