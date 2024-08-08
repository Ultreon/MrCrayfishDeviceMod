package dev.ultreon.devices.impl;

import org.slf4j.LoggerFactory;

public class LoggerImpl implements Logger {
    public final org.slf4j.Logger logger = LoggerFactory.getLogger("DevicesMod");

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void debug(String message, Throwable t) {
        logger.debug(message, t);
    }

    @Override
    public void debug(String message, Object o) {
        logger.debug(message, o);
    }

    @Override
    public void debug(String message, Object o1, Object o2) {
        logger.debug(message, o1, o2);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, Throwable t) {
        logger.info(message, t);
    }

    @Override
    public void info(String message, Object o) {
        logger.info(message, o);
    }

    @Override
    public void info(String message, Object o1, Object o2) {
        logger.info(message, o1, o2);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void warn(String message, Throwable t) {
        logger.warn(message, t);
    }

    @Override
    public void warn(String message, Object o) {
        logger.warn(message, o);
    }

    @Override
    public void warn(String message, Object o1, Object o2) {
        logger.warn(message, o1, o2);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void error(String message, Throwable t) {
        logger.error(message, t);
    }

    @Override
    public void error(String message, Object o) {
        logger.error(message, o);
    }

    @Override
    public void error(String message, Object o1, Object o2) {
        logger.error(message, o1, o2);
    }
}
