package com.ultreon.devices.fabric;

import com.ultreon.devices.event.FeatureFlagRegisterEvent;
import com.ultreon.devices.featuretoggle.DevicesFeatureFlags;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class DevicesPreLaunchFabric implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        FeatureFlagRegisterEvent.EVENT.register(DevicesFeatureFlags::register);
    }
}