package com.ultreon.devices.core;

import com.ultreon.devices.object.AppInfo;

public record PermissionRequest(
        String reason,
        Permission permission,
        AppInfo app
) {

}
