package com.ultreon.devices;

import de.marhali.json5.Json5;
import dev.architectury.platform.Platform;

public class Reference {
    public static final String MOD_ID = "devices";
    public static final String VERSION;
    public static final Json5 JSON5 = Json5.builder(builder -> {
        builder.quoteless();
        builder.indentFactor(4);
        return builder.build();
    });
    private static String[] verInfo;
    static {
        VERSION = getVersion();
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
