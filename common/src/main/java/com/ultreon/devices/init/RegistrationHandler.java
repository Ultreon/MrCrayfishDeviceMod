package com.ultreon.devices.init;

import dev.ultreon.mods.xinexlib.Env;
import dev.ultreon.mods.xinexlib.EnvExecutor;

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
        EnvExecutor.runInEnv(Env.CLIENT, () -> DeviceEntityRenderers::register);
    }
}
