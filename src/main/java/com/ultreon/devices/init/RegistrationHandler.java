package com.ultreon.devices.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

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
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerClient() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> DeviceEntityRenderers::register);
    }
}
