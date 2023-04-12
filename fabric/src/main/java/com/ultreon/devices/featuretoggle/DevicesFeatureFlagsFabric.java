package com.ultreon.devices.featuretoggle;

import net.minecraft.world.flag.FeatureFlagRegistry;

public class DevicesFeatureFlagsFabric implements FeatureFlagRegistration {
    @Override
    public void registerFeatureFlags(FeatureFlagRegistry.Builder builder) {
        DevicesFeatureFlags.register(builder);
    }
}
