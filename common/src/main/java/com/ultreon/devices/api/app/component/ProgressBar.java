package com.ultreon.devices.api.app.component;

import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;

public class ProgressBar extends Component {
    protected int barWidth;
    protected int barHeight;
    protected int progress = 0;
    protected int max = 100;

    protected int progressColor = new Color(189, 198, 255).getRGB();
    protected int backgroundColor = Color.DARK_GRAY.getRGB();
    protected int borderColor = Color.BLACK.getRGB();

    /// Default progress bar constructor
    ///
    /// @param left   how many pixels from the left
    /// @param top    how many pixels from the top
    /// @param width  width of the progress bar
    /// @param height height of the progress bar
    public ProgressBar(int left, int top, int width, int height) {
        super(left, top);
        this.barWidth = width;
        this.barHeight = height;
    }

    @Override
    public void render(GuiGraphics graphics, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            Color bgColor = new Color(getColorScheme().getBackgroundColor(), true);
            graphics.fill(xPosition, yPosition, xPosition + barWidth, yPosition + barHeight, bgColor.darker().darker().getRGB());
            graphics.fill(xPosition + 1, yPosition + 1, xPosition + barWidth - 1, yPosition + barHeight - 1, bgColor.getRGB());
            graphics.fill(xPosition + 2, yPosition + 2, xPosition + 2 + getProgressScaled(), yPosition + barHeight - 2, bgColor.brighter().brighter().getRGB());
        }
    }

    private int getProgressScaled() {
        return (int) Math.ceil(((barWidth - 4) * ((double) progress / (double) max)));
    }

    /// Gets the current progress.
    ///
    /// @return the progress
    public int getProgress() {
        return progress;
    }

    /// Sets the current progress.
    ///
    /// @param progress the progress to set
    public void setProgress(int progress) {
        if (progress > max) {
            progress = max;
        } else if (progress < 0) {
            progress = 0;
        }
        this.progress = progress;
    }

    /// Sets the max progress
    ///
    /// @param max the max progress
    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
        }
    }
}
