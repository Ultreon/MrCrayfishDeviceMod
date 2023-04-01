package com.ultreon.devices.fabric;

import com.ultreon.devices.BuiltinApps;

public class BuiltinAppsRegistration implements FabricApplicationRegistration {
    @Override
    public void registerApplications() {
        BuiltinApps.registerBuiltinApps();
    }
}
