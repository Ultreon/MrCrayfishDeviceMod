package dev.ultreon.mineos.apps.system;

import dev.ultreon.devices.impl.app.Application;
import dev.ultreon.mineos.userspace.MineOS;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Casey on 03-Aug-17.
 */
public abstract class SystemApp extends Application {
    private MineOS laptop;

    SystemApp() {
    }

    public void setOS(@Nullable MineOS laptop) {
        this.laptop = laptop;
    }

    @Nullable
    public MineOS getOS() {
        return laptop;
    }
}
