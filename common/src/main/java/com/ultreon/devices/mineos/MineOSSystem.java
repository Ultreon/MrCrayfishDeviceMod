package com.ultreon.devices.mineos;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.mineos.client.Settings;
import com.ultreon.devices.object.AppInfo;

public interface MineOSSystem {
    Settings getSettings();

    Iterable<AppInfo> getInstalledApplications();

    Application openApplication(AppInfo application);
}
