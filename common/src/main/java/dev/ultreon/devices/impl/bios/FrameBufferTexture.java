package dev.ultreon.devices.impl.bios;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FrameBufferTexture extends AbstractTexture {
    private final int textureId;

    public FrameBufferTexture(int textureId, int width, int height) {
        super();
        this.textureId = textureId;

        bind();
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, width, height, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);

        GL11.glBindTexture(GL30.GL_TEXTURE_2D, GL11.GL_NONE);
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        // No
    }

    @Override
    public int getId() {
        return textureId;
    }

    @Override
    public void close() {
        // No
    }
}
