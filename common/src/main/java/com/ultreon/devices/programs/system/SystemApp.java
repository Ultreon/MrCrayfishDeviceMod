package com.ultreon.devices.programs.system;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.mineos.client.MineOS;

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
