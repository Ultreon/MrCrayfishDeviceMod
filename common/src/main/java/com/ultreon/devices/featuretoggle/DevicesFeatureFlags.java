package com.ultreon.devices.featuretoggle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagRegistry;

public class DevicesFeatureFlags {
    private static FeatureFlag experiments;

    public static FeatureFlag getExperiments() {
        return experiments;
    }

    public static void register(FeatureFlagRegistry.Builder builder) {
        experiments = builder.create(id("experiments"));
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation("devices", path);
    }
}
