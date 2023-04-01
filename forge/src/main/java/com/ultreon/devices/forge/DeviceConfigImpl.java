package com.ultreon.devices.forge;

import com.ultreon.devices.DeviceConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class DeviceConfigImpl {

    public static void register(Object context) {
        ((ModLoadingContext)context).registerConfig(ModConfig.Type.CLIENT, DeviceConfig.CONFIG);
    }
}
