package com.ultreon.devices.api.app;

import it.unimi.dsi.fastutil.Pair;

public record LauncherResponse(
        String error,
        Application app,
        boolean success
) {
}
