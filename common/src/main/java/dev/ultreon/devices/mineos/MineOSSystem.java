package dev.ultreon.devices.mineos;

import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.mineos.client.Settings;
import dev.ultreon.devices.object.AppInfo;

public interface MineOSSystem {
    Settings getSettings();

    Iterable<AppInfo> getInstalledApplications();

    Application openApplication(AppInfo application);
}
