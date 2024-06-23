package com.ultreon.devices.api.app.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class Spinner extends Component {
    protected final int MAX_PROGRESS = 31;
    protected int currentProgress = 0;

    protected Color spinnerColor = Color.WHITE;

    /**
     * Default spinner constructor
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public Spinner(int left, int top) {
        super(left, top);
    }

    @Override
    public void handleTick() {
        if (currentProgress >= MAX_PROGRESS) {
            currentProgress = 0;
        }
        currentProgress++;
    }

    @Override
    public void render(MatrixStack pose, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            RenderSystem.blendColor(1f, 1f, 1f, 1f);
            Color bgColor = new Color(getColorScheme().getBackgroundColor()).brighter().brighter();
            float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
            bgColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], 1f));
            RenderSystem.blendColor(bgColor.getRed() / 255f, bgColor.getGreen() / 255f, bgColor.getBlue() / 255f, 1f);
            mc.textureManager.bind(Component.COMPONENTS_GUI);
            blit(pose, xPosition, yPosition, (currentProgress % 8) * 12, 12 + 12 * (int) Math.floor((double) currentProgress / 8), 12, 12);
            RenderSystem.blendColor(1f, 1f, 1f, 1f);
        }
    }
}
