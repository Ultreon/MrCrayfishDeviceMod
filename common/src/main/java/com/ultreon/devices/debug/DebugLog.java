package com.ultreon.devices.debug;

import com.ultreon.devices.Devices;
import dev.architectury.platform.Platform;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class DebugLog {
    private static final Marker MARKER = MarkerFactory.getMarker("Debugger");

    public static void log(String message) {
        if (Platform.isDevelopmentEnvironment()) {
            Devices.LOGGER.info(message);
        }
    }
    public static void logTime(long ticks, String message) {
        if (Platform.isDevelopmentEnvironment()) {
            Devices.LOGGER.info("(@" + ticks + " ticks) " + message);
        }
    }
}
