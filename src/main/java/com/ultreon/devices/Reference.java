package com.ultreon.devices;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class Reference {
    public static final String MOD_ID = "devices";
    public static final String VERSION;

    static {
        VERSION = getVersion();
    }

    @ExpectPlatform // gets the mod version of "devices"
    public static String getVersion() {
        throw new AssertionError();
    }
}
