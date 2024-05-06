package dev.ultreon.devices.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import static org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D;
import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
    private final FrameBufferTexture texture;
    private final int texId;
    private final int id;
    private final Window window;
    private final TextureManager textureMgr;
    private int width;
    private int height;

    public FrameBuffer(ResourceLocation location, int width, int height) {
        this.texture = new FrameBufferTexture(location, width, height);
        this.texture.upload();
        this.texId = this.texture.getId();

        this.texture.bind();

        this.width = width;
        this.height = height;

        this.window = Minecraft.getInstance().getWindow();
        this.textureMgr = Minecraft.getInstance().getTextureManager();

        this.id = glGenFramebuffersEXT();
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                GL_TEXTURE_2D, texture.getId(), 0);

        int result = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER);
        if (result != GL_FRAMEBUFFER_COMPLETE) {
            glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
            glDeleteFramebuffers(id);

            throw new RuntimeException("Failed to create frame buffer: " + result);
        }

        glBindFramebufferEXT(GL_FRAMEBUFFER, 0);

        this.textureMgr.register(this.texture.getLocation(), this.texture);
    }

    public static boolean isSupported() {
        return glCheckFramebufferStatusEXT(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE;
    }

    public FrameBufferTexture getTexture() {
        return texture;
    }

    public int getTexId() {
        return texId;
    }

    public void blit(GuiGraphics graphics, int x, int y, int width, int height) {
        NativeImage pixels = this.texture.getPixels();
        graphics.blit(this.texture.getLocation(), x, y, width, height, pixels.getWidth(), pixels.getHeight());
    }

    public void dispose() {
        this.textureMgr.release(this.texture.getLocation());
    }

    public void upload() {
        this.texture.upload();
    }

    public void begin() {
        glViewport(0, 0, width, height);
        glBindFramebufferEXT(GL_FRAMEBUFFER, id);
    }

    public void end() {
        glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, window.getWidth(), window.getHeight());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        this.texture.resize(width, height);
    }
}
