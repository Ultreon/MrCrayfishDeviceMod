package com.ultreon.devices.init;

import com.ultreon.devices.entity.renderer.SeatEntityRenderer;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;

public class DeviceEntityRenderers {
    static {
        EntityRendererRegistry.register(DeviceEntities.SEAT::get, SeatEntityRenderer::new);
    }

    public static void register() {

    }
}
