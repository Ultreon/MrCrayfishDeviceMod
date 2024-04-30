package com.ultreon.devices.device;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
    private final int id;
    private final DynamicTexture texture;

    public FrameBuffer(int width, int height) {
        this.texture = new DynamicTexture(new NativeImage(width, height, true));

        this.id = glGenFramebuffersEXT();

        glBindFramebufferEXT(GL_FRAMEBUFFER, this.id);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, texture.getId(), 0);
        int result = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER);
        if (result != GL_FRAMEBUFFER_COMPLETE_EXT) {
            glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
            glDeleteFramebuffersEXT(this.id);

            throw new RuntimeException("Failed to create framebuffer: " + result);
        }
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    public int getId() {
        return this.id;
    }

    /**
     * Binds the FBO and sets glViewport to the texture region width/height.
     */
    public void begin() {
        if (id == 0)
            throw new IllegalStateException("Can't use FBO as it has been destroyed.");
        glViewport(0, 0, getWidth(), getHeight());
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, id);
    }

    /**
     * Unbinds the FBO and resets glViewport to the display size.
     */
    public void end() {
        if (id==0)
            return;
        glViewport(0, 0, Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    }

    private int getWidth() {
        NativeImage pixels = texture.getPixels();
        return pixels != null ? pixels.getHeight() : 0;
    }

    private int getHeight() {
        NativeImage pixels = texture.getPixels();
        return pixels != null ? pixels.getWidth() : 0;
    }

    public void resize(int width, int height) {
        this.texture.setPixels(new NativeImage(width, height, true));
        this.texture.upload();
    }

    public void delete() {
        glDeleteFramebuffersEXT(this.id);
    }

    public DynamicTexture getTexture() {
        return texture;
    }

    public float getU() {
        return 0;
    }

    public float getV() {
        return 1f;
    }

    public float getU2() {
        return 1f;
    }

    public float getV2() {
        return 0;
    }
}
