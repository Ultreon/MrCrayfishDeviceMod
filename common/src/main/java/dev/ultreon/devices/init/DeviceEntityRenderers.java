package dev.ultreon.devices.init;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.ultreon.devices.entity.renderer.SeatEntityRenderer;

public class DeviceEntityRenderers {
    static {
        EntityRendererRegistry.register(DeviceEntities.SEAT::get, SeatEntityRenderer::new);
    }

    public static void register() {

    }
}
