package com.ultreon.devices.util;

import dev.architectury.platform.Platform;

public class ArchUtils {
    @Deprecated
    public static boolean isProduction() {
        return Platform.isDevelopmentOnlyIn();
    }
}
