package com.ultreon.devices.api.driver;

import com.jab125.version.SemanticVersionImpl;
import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.io.Folder;
import com.ultreon.devices.api.task.Callback;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.core.io.action.FileAction;
import com.ultreon.devices.core.io.task.TaskGetMainDrive;
import com.ultreon.devices.core.io.task.TaskSendAction;
import net.minecraft.core.BlockPos;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class PhysicalDiskDriver extends DiskDriver {
    private static final DriverMetadata METADATA = new DriverMetadata(
            "Physical Disk I/O Driver",
            "Driver for physical disks such as internal disks and flash drives.",
            "XyperCode",
            new SemanticVersionImpl(new int[]{1, 0, 0}, null, null)
    );

    public PhysicalDiskDriver() {
        super(METADATA);
    }

    @Override
    public CompletableFuture<? extends Drive> getMainDrive() {
        Task task = new TaskGetMainDrive(Laptop.getPos());
        CompletableFuture<Drive> future = new CompletableFuture<>();
        task.setCallback((tag, success) -> {
            try {
                if (success && tag != null) future.complete(new Drive(tag));
                else future.completeExceptionally(new IOException("Failed to get main drive"));
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        TaskManager.sendTask(task);
        return future;
    }

    @Override
    public CompletableFuture<FileSystem.Response> sendAction(BlockPos pos, Drive drive, FileAction action, Callback<FileSystem.Response> callback) {
        Task task = new TaskSendAction(drive, action);
        CompletableFuture<FileSystem.Response> future = new CompletableFuture<>();
        task.setCallback((tag, success) -> {
            try {
                if (success && tag != null) future.complete(FileSystem.Response.fromTag(tag.getCompound("response")));
                else future.complete(FileSystem.Response.fromTag(tag.getCompound("response")));
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        TaskManager.sendTask(task);
        return future;
    }
}
