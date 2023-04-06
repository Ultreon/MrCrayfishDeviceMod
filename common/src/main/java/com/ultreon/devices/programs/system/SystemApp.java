package com.ultreon.devices.programs.system;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.core.Laptop;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import javax.annotation.Nullable;

/**
 * Created by Casey on 03-Aug-17.
 */
public abstract class SystemApp extends Application {
    @Environment(EnvType.CLIENT)
    private Laptop laptop;

    SystemApp() {
    }

    @Environment(EnvType.CLIENT)
    public void setLaptop(@Nullable Laptop laptop) {
        this.laptop = laptop;
    }

    @Nullable
    @Environment(EnvType.CLIENT)
    public Laptop getLaptop() {
        return laptop;
    }
}
