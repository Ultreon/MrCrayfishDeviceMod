package com.ultreon.devices.init;

import dev.architectury.utils.EnvExecutor;
import net.fabricmc.api.EnvType;

/// @author MrCrayfish
public class RegistrationHandler {
    public static void register() {
        DeviceEntities.register();
        DeviceBlockEntities.register();
        DeviceBlocks.register();
        DeviceTags.register();
        DeviceItems.register();
        DeviceSounds.register();
        DeviceCreativeTabs.register();
        DeviceDataComponents.register();
        EnvExecutor.runInEnv(EnvType.CLIENT, () -> DeviceEntityRenderers::register);
    }
}
