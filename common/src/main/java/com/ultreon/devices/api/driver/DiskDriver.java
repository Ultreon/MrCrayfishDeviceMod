package com.ultreon.devices.api.driver;

import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.task.Callback;
import com.ultreon.devices.api.utils.OnlineRequest;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.action.FileAction;
import net.minecraft.core.BlockPos;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class DiskDriver extends DeviceDriver {
    protected DiskDriver(DriverMetadata metadata) {
        super(metadata);
    }

    public abstract CompletableFuture<? extends Drive> getMainDrive();

    public abstract CompletableFuture<? extends FileSystem.Response> sendAction(BlockPos pos, Drive drive, FileAction action, Callback<FileSystem.Response> callback);
}
