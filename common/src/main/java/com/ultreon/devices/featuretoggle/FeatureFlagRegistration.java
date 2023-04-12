package com.ultreon.devices.featuretoggle;

import net.minecraft.world.flag.FeatureFlagRegistry;
import net.minecraft.world.flag.FeatureFlags;

public interface FeatureFlagRegistration {
    void registerFeatureFlags(FeatureFlagRegistry.Builder builder);
}