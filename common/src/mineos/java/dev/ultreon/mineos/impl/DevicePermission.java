package dev.ultreon.mineos.impl;

import dev.ultreon.mineos.kernel.Device;

public class DevicePermission extends Permission {
    public DevicePermission(Device device, String name) {
        super(name);
    }
}
