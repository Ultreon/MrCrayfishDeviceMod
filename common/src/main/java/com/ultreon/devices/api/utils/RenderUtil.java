package com.ultreon.devices.api.utils;

import net.minecraft.client.renderer.RenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.*;
import com.ultreon.devices.api.app.component.Image;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.object.AppInfo;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.*;

@SuppressWarnings("unused")
public class RenderUtil {
    public static void renderItem(int x, int y, ItemStack stack, boolean overlay) {
        RenderSystem.disableDepthTest();
        // Todo - Port to 1.18.2 if possible
//        RenderSystem.enableLighting();
        RenderHelper.setupForFlatItems();
        //RenderSystem.setShader();
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
        if (overlay)
            Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font, stack, x, y);

        // Todo - Port to 1.18.2 if possible
        //RenderSystem.enableAlpha();
        //Lighting.setupForFlatItems();
    }

    public static void drawIcon(MatrixStack pose, double x, double y, AppInfo info, int width, int height) {
        //Gui.blit(pose, (int) x, (int) y, width, height, u, v, sourceWidth, sourceHeight, (int) textureWidth, (int) textureHeight);
        if (info == null) {
            drawRectWithTexture(pose, x, y, 0, 0, width, height, 14, 14, 224, 224);
            return;
        }
        RenderSystem.enableBlend();
        var glyphs = new AppInfo.Icon.Glyph[]{info.getIcon().getBase(), info.getIcon().getOverlay0(), info.getIcon().getOverlay1()};
        for (AppInfo.Icon.Glyph glyph : glyphs) {
            if (glyph.getU() == -1 || glyph.getV() == -1) continue;
            var col = new Color(info.getTint(glyph.getType()));
            int[] tint = new int[]{col.getRed(), col.getGreen(), col.getBlue()};
            RenderSystem.blendColor(tint[0]/255f, tint[1]/255f, tint[2]/255f, 1f);
            drawRectWithTexture(pose, x, y, glyph.getU(), glyph.getV(), width, height, 14, 14, 224, 224);
            //image.init(layout);
        }
        RenderSystem.blendColor(1f, 1f, 1f, 1f);
    }

    public static void drawRectWithTexture(MatrixStack pose, double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        drawRectWithTexture(pose, x, y, 0, u, v, width, height, textureWidth, textureHeight);
        // Gui.blit(pose, (int) x, (int) y, width, height, u, v, width, height, (int) textureWidth, (int) textureHeight);
    }

    /**
     * Texture size must be 256x256
     *
     * @param pose          the pose stack to draw on
     * @param x             the x position of the rectangle
     * @param y             the y position of the rectangle
     * @param z             the z position of the rectangle
     * @param u             the x position of the texture
     * @param v             the y position of the texture
     * @param width         the width of the rectangle
     * @param height        the height of the rectangle
     * @param textureWidth  the width of the texture
     * @param textureHeight the height of the texture
     */
    public static void drawRectWithTexture(MatrixStack pose, double x, double y, double z, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        //Gui.blit(pose, (int) x, (int) y, width, height, u, v, width, height, (int) textureWidth, (int) textureHeight);
        float scale = 0.00390625f;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        try {
            buffer.begin(GL20.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        } catch (IllegalStateException e) {
            buffer.end();
            buffer.begin(GL20.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        }
        buffer.vertex(x, y + height, z).uv(u * scale, (v + textureHeight) * scale).endVertex();
        buffer.vertex(x + width, y + height, z).uv((u + textureWidth) * scale, (v + textureHeight) * scale).endVertex();
        buffer.vertex(x + width, y, z).uv((u + textureWidth) * scale, v * scale).endVertex();
        buffer.vertex(x, y, z).uv(u * scale, v * scale).endVertex();
        buffer.end();
        BufferUploader.end(buffer);
    }

    public static void drawRectWithFullTexture(MatrixStack pose, double x, double y, float u, float v, int width, int height) {
        // Gui.blit(pose, (int) x, (int) y, width, height, u, v, width, height, 256, 256);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL20.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(x, y + height, 0).uv(0, 1).endVertex();
        buffer.vertex(x + width, y + height, 0).uv(1, 1).endVertex();
        buffer.vertex(x + width, y, 0).uv(1, 0).endVertex();
        buffer.vertex(x, y, 0).uv(0, 0).endVertex();
        buffer.end();
        BufferUploader.end(buffer);
    }

    public static void drawRectWithTexture(MatrixStack pose, double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight, int sourceWidth, int sourceHeight) {
        //Gui.blit(pose, (int) x, (int) y, width, height, u, v, sourceWidth, sourceHeight, (int) textureWidth, (int) textureHeight);
        float scaleWidth = 1f / sourceWidth;
        float scaleHeight = 1f / sourceHeight;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder buffer = Tessellator.getInstance().getBuilder();
        buffer.begin(GL20.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(x, y + height, 0).uv(u * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
        buffer.vertex(x + width, y + height, 0).uv((u + textureWidth) * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
        buffer.vertex(x + width, y, 0).uv((u + textureWidth) * scaleWidth, v * scaleHeight).endVertex();
        buffer.vertex(x, y, 0).uv(u * scaleWidth, v * scaleHeight).endVertex();
        buffer.end();
        BufferUploader.end(buffer);
    }

    public static void drawApplicationIcon(MatrixStack pose, @Nullable AppInfo info, double x, double y) {
        //TODO: Reset color GlStateManager.color(1f, 1f, 1f);
        mc.textureManager.bind(Laptop.ICON_TEXTURES);
        if (info != null) {
            drawIcon(pose, x, y, info, 14, 14);
          //  drawRectWithTexture(pose, x, y, info.getIconU(), info.getIconV(), 14, 14, 14, 14, 224, 224);
        } else {
            drawRectWithTexture(pose, x, y, 0, 0, 14, 14, 14, 14, 224, 224);
        }
    }

    public static void drawStringClipped(MatrixStack pose, String text, int x, int y, int width, int color, boolean shadow) {
        if (shadow) Laptop.getFont().drawShadow(pose, clipStringToWidth(text, width) + TextFormatting.RESET, x, y, color);
        else Laptop.getFont().draw(pose, Laptop.getFont().plainSubstrByWidth(text, width) + TextFormatting.RESET, x, y, color);
    }

    public static String clipStringToWidth(String text, int width) {
        FontRenderer fontRenderer = Laptop.getFont();
        String clipped = text;
        if (fontRenderer.width(clipped) > width) {
            clipped = fontRenderer.plainSubstrByWidth(clipped, width - 8) + "...";
        }
        return clipped;
    }

    public static boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
    }

    public static int color(int color, int defaultColor) {
        return color > 0 ? color : defaultColor;
    }
}
