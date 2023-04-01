package com.ultreon.devices.core;

import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.system.object.ColorScheme;
import net.minecraft.nbt.CompoundTag;

/**
 * @author MrCrayfish
 */
public class Settings {
    private static boolean showAllApps = true;

    private ColorScheme colorScheme = new ColorScheme();

    public static void setShowAllApps(boolean showAllApps) {
        Settings.showAllApps = showAllApps;
    }

    public static boolean isShowAllApps() {
        return Settings.showAllApps;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("showAllApps", showAllApps);
        tag.put("colorScheme", colorScheme.toTag());
        tag.put("tints", appTintInfo());
        return tag;
    }

    private CompoundTag appTintInfo() {
        var ct = new CompoundTag();
        for (AppInfo installedApplication : Laptop.getSystem().getInstalledApplications()) {
            ct.put(installedApplication.getId().toString(), installedApplication.getTintProvider().toTag());
        }
        return ct;
    }

    public static Settings fromTag(CompoundTag tag) {
        //showAllApps = tag.getBoolean("showAllApps");

        Settings settings = new Settings();
        settings.colorScheme = ColorScheme.fromTag(tag.getCompound("colorScheme"));
        return settings;
    }
}
