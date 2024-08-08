package dev.ultreon.devices.impl.bios;

import dev.ultreon.devices.UltreonDevicesMod;
import dev.ultreon.devices.api.bios.FrameBufferCall;
import dev.ultreon.devices.api.bios.FrameBufferInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

public class FrameBuffer {
    public static final int RESIZE = 0;
    public static final int DISABLE = 1;
    public static final int ENABLE = 2;
    private GuiGraphics guiGraphics;
    private int fboId;

    private int depthBufferId;
    private int stencilBufferId;

    int width;
    int height;
    private AbstractTexture texture;
    private ResourceLocation texturePath;

    public FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Object call(FrameBufferCall call, Object... data) {
        switch (call) {
            case GET_INFO -> {
                int width = this.width;
                int height = width * 9 / 16;
                return new FrameBufferInfo(width, height, 32);
            }
            case READ_PIXELS -> {
                ByteBuffer buffer = (ByteBuffer) data[0];
                int x = (int) data[1];
                int y = (int) data[2];
                int width = (int) data[3];
                int height2 = (int) data[4];
                bind();
                GL30.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
                GL30.glReadPixels(x, y, width, height2, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer);
                unbind();
                return null;
            }
            case READ_PIXEL -> {
                int x = (int) data[0];
                int y = (int) data[1];
                int[] color = new int[1];
                bind();
                GL30.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
                GL30.glReadPixels(x, y, 1, 1, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, color);
                unbind();
                return color[0];
            }
            case DRAW_RECT -> {
                int x = (int) data[0];
                int y = (int) data[1];
                int width = (int) data[2];
                int height = (int) data[3];
                int color = (int) data[4];
                bind();
                guiGraphics.renderOutline(x, y, x + width, y + height, color);
                unbind();
                return null;
            }
            case FILL_RECT -> {
                int x = (int) data[0];
                int y = (int) data[1];
                int width = (int) data[2];
                int height = (int) data[3];
                int color = (int) data[4];
                bind();
                guiGraphics.fill(x, y, x + width, y + height, color);
                unbind();
                return null;
            }
            case DRAW_TEXT -> {
                int x = (int) data[0];
                int y = (int) data[1];
                String text = (String) data[2];
                int color = (int) data[3];
                bind();
                guiGraphics.drawString(Minecraft.getInstance().font, text, x, y, color);
                unbind();
                return null;
            }
            case WRITE_PIXEL -> {
                int x = (int) data[0];
                int y = (int) data[1];
                int color = (int) data[2];
                bind();
//                GL30.glDrawPixels(x, y, 1, 1, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, color);
                // Hehe
                unbind();
                return null;
            }
            case DRAW_ITEM -> {
                String registryName = (String) data[0];
                int count = (int) data[1];
                int x = (int) data[2];
                int y = (int) data[3];

                ResourceLocation resourceLocation = new ResourceLocation(registryName);
                Item item = UltreonDevicesMod.REGISTRIES.get().get(BuiltInRegistries.ITEM).get(resourceLocation);

                bind();
                if (item != null)
                    guiGraphics.renderItem(new ItemStack(item, count), x, y);
                unbind();

                return null;
            }
            case DRAW_IMAGE -> {
                String registryName = (String) data[0];
                int x = (int) data[1];
                int y = (int) data[2];

                ResourceLocation resourceLocation = new ResourceLocation(registryName);
                Item item = BuiltInRegistries.ITEM.get(resourceLocation);

                bind();
                guiGraphics.renderItem(new ItemStack(item), x, y);
                unbind();

                return null;
            }
            case CLEAR -> {
                bind();
                GL11.glClearColor(0, 0, 0, 0);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                unbind();
                return null;
            }
            case BLIT -> {
                String registryName = (String) data[0];
                int x = (int) data[1];
                int y = (int) data[2];
                int width = (int) data[3];
                int height = (int) data[4];
                int srcX = (int) data[5];
                int srcY = (int) data[6];
                int srcWidth = (int) data[7];
                int srcHeight = (int) data[8];
                int texWidth = (int) data[9];
                int texHeight = (int) data[10];

                ResourceLocation resourceLocation = new ResourceLocation(registryName);

                bind();
                guiGraphics.blit(resourceLocation, x, y, width, height, srcX, srcY, srcWidth, srcHeight, texWidth, texHeight);
                unbind();
                return null;
            }

            default -> {
                return null;
            }
        }
    }

    public void bind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
    }

    public static void unbind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void dispose() {
        GL30.glDeleteFramebuffers(fboId);
        GL30.glDeleteRenderbuffers(depthBufferId);
        GL30.glDeleteRenderbuffers(stencilBufferId);

        GL11.glDeleteTextures(texture.getId());

        texture = null;

        Minecraft.getInstance().getTextureManager().release(texturePath);
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public void create(int width, int height) {
        this.width = width;
        this.height = height;

        this.fboId = GL30.glGenFramebuffers();
        int textureId = GL11.glGenTextures();
        AbstractTexture texture = new FrameBufferTexture(textureId, width, height);

        this.depthBufferId = GL30.glGenRenderbuffers();
        this.stencilBufferId = GL30.glGenRenderbuffers();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
        GL30.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, textureId, 0);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBufferId);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, stencilBufferId);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        this.guiGraphics = new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());
        ResourceLocation texturePath = new ResourceLocation("textures/gui/framebuffer_" + System.currentTimeMillis() + ".png");

        Minecraft.getInstance().getTextureManager().register(texturePath, texture);
        this.texturePath = texturePath;

        this.texture = texture;
    }

    public AbstractTexture getTexture() {
        return texture;
    }
}
