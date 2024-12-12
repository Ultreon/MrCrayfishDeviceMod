package com.ultreon.devices.api.app;

public record LauncherResponse(
        String error,
        Application app,
        boolean success
) {
}
