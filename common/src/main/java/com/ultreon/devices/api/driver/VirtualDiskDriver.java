package com.ultreon.devices.api.driver;

import com.jab125.version.SemanticVersionImpl;
import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.io.VirtualDrive;
import com.ultreon.devices.api.task.Callback;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.action.FileAction;
import net.minecraft.core.BlockPos;

import java.util.concurrent.CompletableFuture;

public final class VirtualDiskDriver extends DiskDriver {
    private static final DriverMetadata METADATA = new DriverMetadata(
            "Virtual Disk I/O Driver",
            "Driver for virtual disks which are not backed by a physical disk such as temporary disks, RAM disks, etc.",
            "XyperCode",
            new SemanticVersionImpl(new int[]{1, 0, 0}, null, null)
    );

    public VirtualDiskDriver() {
        super(METADATA);
    }

    @Override
    public CompletableFuture<? extends Drive> getMainDrive() {
        return CompletableFuture.completedFuture(new VirtualDrive());
    }

    @Override
    public CompletableFuture<? extends FileSystem.Response> sendAction(BlockPos pos, Drive drive, FileAction action, Callback<FileSystem.Response> callback) {
        return CompletableFuture.completedFuture(new FileSystem.Response(FileSystem.Status.FAILED, "Not Implemented"));
    }
}
