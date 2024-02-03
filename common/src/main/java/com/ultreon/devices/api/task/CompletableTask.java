package com.ultreon.devices.api.task;

import com.ultreon.devices.core.task.TaskInstallApp;
import com.ultreon.devices.debug.DebugLog;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.concurrent.CompletableFuture;

/**
 * <p>A Task is simple implementation that allows you to make calls to the
 * server to process actions, store or retrieve data, etc. Useful for any
 * client-server like applications, e.g. Emails, Instant Messaging, etc</p>
 *
 * <p>Any global variables that are initialized in this class, wont be on the server side.
 * To initialize them, first store the data in the NBT tag provided in {@link #prepareRequest(CompoundTag)},
 * then once your Task gets to the server, use {@link #processRequest(CompoundTag, Level, Player)} to
 * get the data from the NBT tag parameter. Initialize the variables as normal.
 *
 * <p>Please check out the example applications to get a better understanding
 * how this could be useful to your application.</p>
 *
 * @author MrCrayfish
 */
public abstract class CompletableTask<T> extends Task {
    final CompletableFuture<T> future;

    public CompletableTask(String name) {
        super(name);
        future = new CompletableFuture<>();
    }
    /**
     * Called when the response arrives to the client. Here you can update data
     * on the client side. If you want to update any UI component, you should set
     * a Callback before you sendTask the request. See {@link #setCallback(Callback)}
     *
     * @param tag The NBT Tag received from the server
     */
    public final void processResponse(CompoundTag tag) {
        this.processResponse(tag, new ResultSetter<T>(this));
    }

    public abstract void processResponse(CompoundTag tag, ResultSetter<T> result);

    public static class ResultSetter<T> {
        private final CompletableTask<T> task;

        private ResultSetter(CompletableTask<T> task) {
            this.task = task;
        }

        public void set(T result) {
            task.future.complete(result);
        }

        public void set(Exception e) {
            task.future.completeExceptionally(e);
        }
    }
}
