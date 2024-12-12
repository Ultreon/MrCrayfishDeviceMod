package com.ultreon.devices.init;

import com.ultreon.devices.entity.Seat;
import com.ultreon.devices.entity.renderer.SeatEntityRenderer;
import dev.ultreon.mods.xinexlib.platform.Services;

public class DeviceEntityRenderers {
    static {
        Services.PLATFORM.client().entityRenderers().register(DeviceEntities.SEAT::get, SeatEntityRenderer::new);
    }

    public static void register() {

    }
}
