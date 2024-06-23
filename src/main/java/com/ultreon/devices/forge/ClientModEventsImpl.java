package com.ultreon.devices.forge;

import com.ultreon.devices.object.AppInfo;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class ClientModEventsImpl {
    public static void setRenderLayer(Block block, RenderType renderType) {
        ItemBlockRenderTypes.setRenderLayer(block, renderType);
    }

    public static void updateIcon(AppInfo info, int iconU, int iconV) {
        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconU, "iconU");
        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconV, "iconV");
    }
}
