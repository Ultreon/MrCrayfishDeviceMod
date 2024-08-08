package dev.ultreon.devices.impl.bios;

import dev.ultreon.devices.api.IO;
import dev.ultreon.devices.impl.device.PhysicalHardwareDevice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class DisplayDevice extends Screen implements PhysicalHardwareDevice {
    private static final int SIG_TURN_ON = 0x49;
    private static final int SIG_TURN_OFF = 0x4A;
    private static final VideoMode[] ALL_ = PredefinedResolution.values();
    private final FrameBuffer frameBuffer;
    private boolean enabled;
    private final VideoMode[] modes;
    private transient int modeSelect = 0;
    private final IO io = new IO() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> enabled ? 1 : 0;
                case 1 -> modes.length & 0xFF; // short[0]
                case 2 -> modes.length >> 8 & 0xFF; // short[1]
                default -> 0;
            };
        }

        @Override
        public boolean set(int index, int value) {
            return switch (index) {
                case 0 -> {
                    enabled = value != 0;
                    yield true;
                }
                case 1, 2 -> {
                    if (index == 1) {
                        modeSelect = (value & 0xFF) | ((modeSelect & 0xFF00));
                    } else {
                        modeSelect = (modeSelect & 0x00FF) | ((value & 0xFF) << 8);
                    }
                    yield false;
                }
                default -> false;
            };
        }

        @Override
        public long size() {
            return 1;
        }
    };

    public DisplayDevice(VBios vbios) {
        super(Component.literal("VDisplay"));
        this.frameBuffer = vbios.getFrameBuffer();

        int maxWidth = this.width;
        int maxHeight = this.height;

        List<VideoMode> modes = new ArrayList<>();
        for (VideoMode mode : ALL_) {
            if (mode.getHorizontalResolution() <= maxWidth && mode.getVerticalResolution() <= maxHeight) {
                modes.add(mode);
            }
        }

        this.modes = modes.toArray(new VideoMode[0]);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);

        if (this.frameBuffer != null) {
            this.frameBuffer.dispose();
            this.frameBuffer.create(width, height);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        AbstractTexture texture = this.frameBuffer.getTexture();
        texture.bind();
    }

    @Override
    public void sendSignal(int signal) {
        switch (signal) {
            case SIG_TURN_ON -> this.enabled = true;
            case SIG_TURN_OFF -> this.enabled = false;
        }
    }

    @Override
    public IO io() {
        return this.io;
    }

    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }
}
