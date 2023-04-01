package com.ultreon.devices.core.network.task.forge;

public class TaskGetDevicesImpl {
    public static Class<?> getClass(String string) throws ClassNotFoundException {
        return Class.forName(string);
    }
}
