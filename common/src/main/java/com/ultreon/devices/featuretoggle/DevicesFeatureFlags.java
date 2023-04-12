package com.ultreon.devices.featuretoggle;

import com.ultreon.devices.Devices;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagRegistry;

public class DevicesFeatureFlags {
    private static FeatureFlag printer;

    public static FeatureFlag getPrinter() {
        return printer;
    }

    public static void register(FeatureFlagRegistry.Builder builder) {
        printer = builder.create(id("printer"));
    }

    private static ResourceLocation id(String printer) {
        return new ResourceLocation("devices", printer);
    }
}
