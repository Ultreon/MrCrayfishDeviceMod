package com.ultreon.devices.api.app;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

public interface IIcon {
    ResourceLocation getIconAsset();

    int getIconSize();

    int getGridWidth();

    int getGridHeight();

    /**
     * Width of the source texture in pixels.
     *
     * @return The source width.
     */
    int getSourceWidth();

    /**
     * Height of the source texture in pixels.
     *
     * @return The source height.
     */
    int getSourceHeight();

    int getU();

    int getV();

    int getOrdinal();

    @Environment(EnvType.CLIENT)
    default void draw(PoseStack pose, Minecraft mc, int x, int y) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, getIconAsset());
        int size = getIconSize();
        int assetWidth = getGridWidth() * size;
//        java.lang.System.out.println("assetWidth = " + assetWidth);
        int assetHeight = getGridHeight() * size;
//        java.lang.System.out.println("assetHeight = " + assetHeight);
//        GuiComponent.blit(pose, x + contentX, y + iconY, iconWidth, iconHeight, iconU, iconV, iconWidth, iconHeight, iconSourceWidth, iconSourceHeight);
        GuiComponent.blit(pose, x, y, size, size, getU(), getV(), size, size, assetWidth, assetHeight);
    }
}
