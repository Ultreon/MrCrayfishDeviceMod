package com.ultreon.devices.quilt;

import com.ultreon.devices.BuiltinApps;

public class BuiltinAppsRegistration implements QuiltApplicationRegistration {
    @Override
    public void registerApplications() {
        BuiltinApps.registerBuiltinApps();
    }
}
