package com.ultreon.devices.core.network.task.fabric;

public class TaskGetDevicesImpl {
    public static Class<?> getClass(String string) throws ClassNotFoundException {
        return TaskGetDevicesImpl.class.getClassLoader().loadClass(string);
    }
}
