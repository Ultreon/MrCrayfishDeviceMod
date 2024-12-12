package com.ultreon.devices.fabric;

import com.ultreon.devices.DevicesModPlatform;
import net.fabricmc.loader.api.FabricLoader;

public class DevicesModFabricPlatform implements DevicesModPlatform {
    @Override
    public String getVersion() {
        return FabricLoader.getInstance().getModContainer("devices").orElseThrow().getMetadata().getVersion().getFriendlyString();
    }
}
