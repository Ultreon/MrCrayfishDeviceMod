package com.ultreon.devices;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import dev.architectury.platform.Platform;

public class Reference {
    public static final String MOD_ID = "devices";
    public static final String VERSION;
    public static final File BROWSER_DATA = new File(Platform.getGameFolder().toFile(), "devices-browser-data");
    public static final File CEF_INSTALL = new File(Platform.getGameFolder().toFile(), "devices-browser-install");
    private static String[] verInfo;
    static {
        VERSION = getVersion();
        if (!BROWSER_DATA.exists() && !BROWSER_DATA.mkdirs()) {
            throw new RuntimeException("Can't create browser data directories.");
        }
    }

    public static String getVersion() {
        return Platform.getMod(Devices.MOD_ID).getVersion();
    }

    public static String[] getVerInfo() {
        if (verInfo == null) {
            if (getVersion().split("\\+").length == 1) {
                return verInfo = new String[]{getVersion(), "unknown"};
            }
            var version = getVersion().split("\\+")[0];
            var build = getVersion().split("\\+")[1];
            verInfo = new String[]{version, build};
        }
        return verInfo;
    }
}
