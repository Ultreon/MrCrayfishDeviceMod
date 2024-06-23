package com.ultreon.devices.core;

import com.ultreon.devices.programs.system.object.ColorScheme;
import net.minecraft.nbt.CompoundNBT;

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

    public CompoundNBT toTag() {
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean("showAllApps", showAllApps);
        tag.put("colorScheme", colorScheme.toTag());
        return tag;
    }

    public static Settings fromTag(CompoundNBT tag) {
        //showAllApps = tag.getBoolean("showAllApps");

        Settings settings = new Settings();
        settings.colorScheme = ColorScheme.fromTag(tag.getCompound("colorScheme"));
        return settings;
    }
}
