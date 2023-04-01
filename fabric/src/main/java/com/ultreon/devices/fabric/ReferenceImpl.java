package com.ultreon.devices.fabric;

import com.ultreon.devices.Devices;
import net.fabricmc.loader.api.FabricLoader;

public class ReferenceImpl {
    public static String getVersion() {
        return FabricLoader.getInstance().getModContainer(Devices.MOD_ID).get().getMetadata().getVersion().getFriendlyString();
    }
}
