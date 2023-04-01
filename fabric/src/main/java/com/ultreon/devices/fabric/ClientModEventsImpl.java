package com.ultreon.devices.fabric;

import com.ultreon.devices.object.AppInfo;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public class ClientModEventsImpl {
    public static void setRenderLayer(Block block, RenderType renderType) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
    }

    public static void updateIcon(AppInfo info, int iconU, int iconV) {
//        info.setIconU(iconU);
//        info.setIconV(iconV);
    }
}
