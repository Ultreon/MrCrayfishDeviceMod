package com.ultreon.devices.programs.system.object;

import com.ultreon.devices.object.AppInfo;

import javax.annotation.Nullable;

/**
 * @author MrCrayfish
 */
public interface AppEntry {
    String id();

    String name();

    String author();

    String description();

    @Nullable
    String version();

    @Nullable
    AppInfo.Icon icon();

    @Nullable
    String[] screenshots();
}
