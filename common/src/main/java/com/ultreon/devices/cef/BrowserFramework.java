package com.ultreon.devices.cef;

import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.Devices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL12.*;

public class BrowserFramework {
    private static CefApp app;
    private static CefClient client;
    private static RawTexture texture;
    static Dimension dimension = new Dimension(1, 1);
    private static JFrame frame;
    private static ByteBuffer lastBuffer;

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

    @Deprecated
    public static Component getUi(CefBrowser browser) {
        return browser.getUIComponent();
    }

    public static void redraw() {
        paint();
    }

    public static void paint() {
        BrowserGraphics.locked(() -> {
            ByteBuffer buffer = BrowserGraphics.buffer;
            if (buffer == null) {
                return;
            }

            // Upload buffer data as RGAB to LWJGL3 texture.
            // We don't use Buffered Image because it's not compatible with LWJGL3, and otherwise would be way slower.
            try {
                redrawUnsafe(buffer);
            } catch (IOException e) {
                Devices.criticalCrash(e);
                throw new Error("Unreachable");
            }
        });
    }

    private static void redrawUnsafe(ByteBuffer buffer) throws IOException {
        if (texture == null) {
            var rawTexture = new RawTexture(dimension.width, dimension.height, buffer);
            lastBuffer = buffer;

            Minecraft.getInstance().getTextureManager().register(BrowserGraphics.RES, rawTexture);
            texture = rawTexture;
        } else {
            if (buffer != lastBuffer) {
                reinitBuffer(buffer);
                lastBuffer = buffer;
            } else {
                return;
            }

            texture.update(buffer);
        }
    }

    private static void reinitBuffer(ByteBuffer buffer) {
        texture.reinit(buffer);
        texture.upload();
    }

    public static void renderBrowser(GuiGraphics gfx, int x, int y, int w, int h) {
        gfx.fill(x, y, w, h, 0xff000000);

        if (texture == null) return;

        gfx.blit(BrowserGraphics.RES, x, y, w, h, 0, 0, dimension.width, dimension.height, dimension.width, dimension.height);
        gfx.drawString(Minecraft.getInstance().font, "x: " + x + " y: " + y + " w: " + w + " h: " + h, x + 5, y + 5, 0xffffffff);
    }

    public static void setSize(CefBrowser browser, int width, int height) {
        var dimension = new Dimension(width, height);

        browser.getUIComponent().setPreferredSize(dimension);
        browser.getUIComponent().setMinimumSize(dimension);
        browser.getUIComponent().setMaximumSize(dimension);
        browser.getUIComponent().setSize(dimension);
        browser.getUIComponent().revalidate();
    }

    public static CefBrowser createBrowser() {
        CefBrowser browser = client.createBrowser("www.google.com", true, false);


        SwingUtilities.invokeLater(() -> {
            java.awt.Component uiComponent = browser.getUIComponent();
            GLCanvas canvas = (GLCanvas) uiComponent;
            GLEventListener glEventListener = canvas.getGLEventListener(0);
            canvas.removeGLEventListener(glEventListener);
            canvas.addGLEventListener(new BrowserGraphics(canvas, glEventListener));

            frame.add(uiComponent);
            frame.revalidate();
        });
        return browser;
    }

    public static void closeBrowser(CefBrowser browser) {
        SwingUtilities.invokeLater(() -> {
            browser.close(true);
            frame.remove(browser.getUIComponent());
            frame.revalidate();
        });
    }

    public static void init(JFrame jFrame) {
        BrowserFramework.frame = jFrame;
    }

    public static int getTextureId() {
        return texture.getId();
    }

    public static RawTexture getTexture() {
        return texture;
    }

    public static class RawTexture extends AbstractTexture {
        private ByteBuffer buffer;
        private final long width;
        private final long height;
        private long pixels;

        public RawTexture(long width, long height, ByteBuffer buffer) {
            super();
            this.width = width;
            this.height = height;

            this.pixels = MemoryUtil.nmemAlloc(buffer.capacity());
            this.buffer = MemoryUtil.memByteBufferSafe(this.pixels, buffer.capacity());
            if (this.buffer == null)
                throw new IOError(new IOException("Failed to allocate texture buffer"));
            int capacity = buffer.capacity();
            int remaining = this.buffer.remaining();
            try {
                this.buffer.put(buffer);

                if (this.buffer.remaining() > 0) {
                    Devices.LOGGER.warn("Texture buffer size mismatch: " + this.buffer.remaining() + " vs " + capacity);
                }
            } catch (Exception e) {
                Devices.LOGGER.error("Failed to upload texture buffer");
                Devices.LOGGER.info("Texture size: " + width + " × " + height);
                Devices.LOGGER.info("Buffer capacity: " + capacity);
                Devices.LOGGER.info("Write capacity: " + remaining);
                Devices.criticalCrash(e);
                throw new Error("Unreachable");
            }
        }

        @Override
        public void load(@NotNull ResourceManager resourceManager) throws IOException {
            upload();
        }

        private void upload() {
            RenderSystem.assertOnRenderThreadOrInit();
            this.checkAllocated();
            Devices.LOGGER.info("Uploading texture: " + this.width + " × " + this.height);
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, getId());
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, (int) this.width, (int) this.height, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8, this.buffer);
            glDisable(GL_TEXTURE_2D);
            Devices.LOGGER.info("Uploaded texture: " + this.width + " × " + this.height);
        }

        private void checkAllocated() {
            if (this.pixels == 0L) {
                throw new IllegalStateException("Texture is not allocated");
            }
        }

        public void update(ByteBuffer buffer) {
            this.checkAllocated();
            this.buffer.clear();
            this.buffer.put(buffer);
            this.upload();
        }

        public void reinit(ByteBuffer buffer) {
            this.buffer.clear();
            MemoryUtil.nmemFree(this.pixels);
            this.pixels = MemoryUtil.nmemAlloc(buffer.capacity());
            this.buffer = MemoryUtil.memByteBufferSafe(this.pixels, buffer.capacity());
            if (this.buffer == null)
                throw new IOError(new IOException("Failed to allocate texture buffer"));
            int capacity = buffer.capacity();
            int remaining = this.buffer.remaining();
            try {
                this.buffer.put(buffer);
            } catch (Exception e) {
                Devices.LOGGER.error("Failed to upload texture buffer");
                Devices.LOGGER.info("Texture size: " + width + " × " + height);
                Devices.LOGGER.info("Buffer capacity: " + capacity);
                Devices.LOGGER.info("Write capacity: " + remaining);
                Devices.criticalCrash(e);
                throw new Error("Unreachable");
            }
        }
    }
}
