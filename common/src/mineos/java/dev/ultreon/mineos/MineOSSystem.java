package dev.ultreon.mineos;

import dev.ultreon.mineos.api.Application;
import dev.ultreon.mineos.userspace.Settings;
import dev.ultreon.mineos.object.AppInfo;

public interface MineOSSystem {
    Settings getSettings();

    Iterable<AppInfo> getInstalledApplications();

    Application openApplication(AppInfo application);
}
