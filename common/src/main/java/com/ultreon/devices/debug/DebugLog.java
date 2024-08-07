package com.ultreon.devices.debug;

import dev.architectury.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class DebugLog {
    private static final Logger LOGGER = LoggerFactory.getLogger("DevicesMod:Debug");

    public static void trace(String message) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.trace(message);
        }
    }

    public static void trace(Throwable e) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.trace("", e);
        }
    }

    public static void trace(String message, Throwable e) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.trace(message, e);
        }
    }

    public static void debug(String message) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.debug(message);
        }
    }

    public static void debug(Throwable e) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.debug("", e);
        }
    }

    public static void debug(String message, Throwable e) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.debug(message, e);
        }
    }

    public static void info(String message) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.info(message);
        }
    }

    public static void info(Throwable e) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.info("", e);
        }
    }

    public static void info(String message, Throwable e) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.info(message, e);
        }
    }

    public static void warn(String message) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.warn(message);
        }
    }

    public static void warn(Throwable e) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.warn("", e);
        }
    }

    public static void warn(String message, Throwable e) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.warn(message, e);
        }
    }

    public static void error(String message) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.error(message);
        }
    }

    public static void error(Throwable e) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.error("", e);
        }
    }

    public static void error(String message, Throwable e) {
        if (Platform.isDevelopmentEnvironment()) {
            LOGGER.error(message, e);
        }
    }
}
