package com.ultreon.devices.init;

import com.ultreon.devices.entity.renderer.SeatEntityRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class DeviceEntityRenderers {
    public static void register() {
        RenderingRegistry.registerEntityRenderingHandler(DeviceEntities.SEAT.get(), SeatEntityRenderer::new);
    }
}
