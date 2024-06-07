package dev.ultreon.devices.api;

public interface Logger {
    void debug(String message);

    void debug(String message, Throwable t);

    void debug(String message, Object o);

    void debug(String message, Object o1, Object o2);

    void info(String message);

    void info(String message, Throwable t);

    void info(String message, Object o);

    void info(String message, Object o1, Object o2);

    void warn(String message);

    void warn(String message, Throwable t);

    void warn(String message, Object o);

    void warn(String message, Object o1, Object o2);

    void error(String message);

    void error(String message, Throwable t);

    void error(String message, Object o);

    void error(String message, Object o1, Object o2);
}
