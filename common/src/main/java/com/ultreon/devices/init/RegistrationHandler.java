package com.ultreon.devices.init;

import net.minecraftforge.api.distmarker.DistExecutor;
import net.minecraftforge.api.distmarker.Dist;

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
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> DeviceEntityRenderers::register);
    }
}
