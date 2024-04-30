package com.ultreon.devices.api;

import com.ultreon.devices.UltreonDevicesMod;

public class DebugLog {
    public static void log(String message) {
        if (UltreonDevicesMod.get().isDebug()) {
            UltreonDevicesMod.LOGGER.debug(message);
        }
    }
}
