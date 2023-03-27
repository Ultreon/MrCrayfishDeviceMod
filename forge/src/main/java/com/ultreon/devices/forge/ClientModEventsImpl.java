package com.ultreon.devices.forge;

import com.ultreon.devices.object.AppInfo;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class ClientModEventsImpl {
    public static void setRenderLayer(Block block, RenderType renderType) {
        RenderTypeRegistry.register(renderType, block);
    }

    public static void updateIcon(AppInfo info, int iconU, int iconV) {
        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconU, "iconU");
        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconV, "iconV");
    }
}
