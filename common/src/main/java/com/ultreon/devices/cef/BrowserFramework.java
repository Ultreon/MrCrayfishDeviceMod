package com.ultreon.devices.cef;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.Devices;
import com.ultreon.mods.lib.util.KeyboardHelper;
import dev.architectury.platform.Platform;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.cef.CefApp;
import org.cef.CefClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class BrowserFramework {
    public static final ResourceLocation RES = Devices.id("dynamic/app/browser");
    static BufferedImage bufImg = null;

    private static CefApp app;
    private static CefClient client;
    private static Component ui;
    private static NativeImage img;
    private static DynamicTexture texture;
    private static int texId;
    private static long lastCapture;

    static CefApp setApp(CefApp app) {
        BrowserFramework.app = app;
        return app;
    }

    public static CefApp getApp() {
        return app;
    }

    static CefClient setClient(CefClient client) {
        BrowserFramework.client = client;
        return client;
    }

    public static CefClient getClient() {
        return client;
    }

    static Component setUi(Component ui) {
        BrowserFramework.ui = ui;
        return ui;
    }

    public static Component getUi() {
        return ui;
    }

    public static void redraw() {
        if (texture == null) {
            texture = new DynamicTexture(paint());
            texId = texture.getId();
            Minecraft.getInstance().getTextureManager().register(RES, texture);
            return;
        }
        texture.setPixels(paint());
        NativeImage pixels = texture.getPixels();
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
                TextureUtil.prepareImage(texture.getId(), pixels.getWidth(), pixels.getHeight());
                texture.upload();
            });
        } else {
            TextureUtil.prepareImage(texture.getId(), pixels.getWidth(), pixels.getHeight());
            texture.upload();
        }
    }

    public static NativeImage paint() {
        BufferedImage bufferedImage = Objects.requireNonNullElseGet(bufImg, () -> ComponentPainter.paintComponent(BrowserFramework.getUi()));
        byte[] bytes;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", out);
            bytes = out.toByteArray();
            out.close();

            // START :: DEBUG
            var now = System.currentTimeMillis() / 1000;
            long l = now - lastCapture;
            if (KeyboardHelper.isKeyDown(InputConstants.KEY_F10) && l > 5) {
                Devices.LOGGER.info("Capturing web browser image.");
                String textDateTime = Util.getFilenameFormattedDateTime();
                String captureLocation = "screenshots/devices-mod-browser-capture_" + textDateTime + ".png";
                File file = new File(Platform.getGameFolder().toFile(), captureLocation);
                ImageIO.write(bufferedImage, "PNG", file);
                lastCapture = now;
            }
            // END :: DEBUG
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            img = NativeImage.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return img;
    }

    public static void renderBrowser(PoseStack poseStack, int x, int y, int w, int h) {
        RenderSystem.setShaderTexture(0, RES);
        GuiComponent.blit(poseStack, x, y, w, h, 0, 0, img.getWidth(), img.getHeight(), img.getWidth(), img.getHeight());
    }
}
