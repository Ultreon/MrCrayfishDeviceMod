package com.ultreon.devices.util;

import net.minecraftforge.fml.loading.FMLEnvironment;

public class ArchUtils {
    @Deprecated
    public static boolean isProduction() {
        return FMLEnvironment.production;
    }
}
