package com.ultreon.devices.init;

import dev.architectury.utils.EnvExecutor;
import net.fabricmc.api.EnvType;

/**
 * @author MrCrayfish
 */
public class RegistrationHandler {
    public static void register() {
        DeviceEntities.register();
        DeviceBlockEntities.register();
        DeviceBlocks.register();
        DeviceItems.register();
        DeviceSounds.register();
        EnvExecutor.runInEnv(EnvType.CLIENT, () -> DeviceEntityRenderers::register);
    }
}
