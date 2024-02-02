package com.ultreon.devices.programs.browser;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.cef.BrowserFramework;
import com.ultreon.devices.cef.BrowserGraphics;
import com.ultreon.devices.core.Laptop;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.cef.browser.CefBrowser;

import java.awt.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class BrowserRenderer extends Component {
    protected boolean initialized = false;
    protected boolean drawFull = false;
    protected int imageU, imageV;
    protected int imageWidth, imageHeight;
    protected int sourceWidth, sourceHeight;
    public int componentWidth;
    public int componentHeight;
    private float alpha = 1f;
    private Supplier<ColorSupplier> tint = () -> Util.make(new ColorSupplier(), cs -> {
        cs.r = 255;
        cs.g = 255;
        cs.b = 255;
    });
    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setTint(int r, int g, int b) {
        var cs = new ColorSupplier();
        cs.r = r;
        cs.g = g;
        cs.b = b;
        this.setTint(() -> cs);
    }

    public static class ColorSupplier {

        int r;
        int g;
        int b;
    }
    public void setTint(Supplier<ColorSupplier> colorSupplier) {
        this.tint = colorSupplier;
    }


    private boolean hasBorder = false;
    private int borderColor = Color.BLACK.getRGB();
    private int borderThickness = 0;

    public BrowserRenderer(CefBrowser browser, java.awt.Component ui, int left, int top, int componentWidth, int componentHeight, int imageU, int imageV, int imageWidth, int imageHeight, int sourceWidth, int sourceHeight) {
        super(left, top);
        this.componentWidth = componentWidth;
        this.componentHeight = componentHeight;
        this.imageU = imageU;
        this.imageV = imageV;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        this.visible = false;
    }

    @Override
    public void init(Layout layout) {
        initialized = true;
    }

    @Override
    public void handleLoad() {

    }

    @Override
    protected void handleUnload() {
        this.initialized = false;
    }

    @Override
    public void render(GuiGraphics gfx, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            if (hasBorder) {
                gfx.fill(x, y, x + componentWidth, y + componentHeight, 0xff000000);
            }

            RenderSystem.setShaderColor(tint.get().r / 255f, tint.get().g / 255f, tint.get().b / 255f, alpha);

            BrowserFramework.RawTexture texture = BrowserFramework.getTexture();

            gfx.fill(x, y, x + componentWidth, y + componentHeight, 0xff000000);
            if (texture != null && texture.getId() != -1) {
                RenderSystem.setShaderColor(1, 1, 1, alpha);
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, BrowserGraphics.RES);

                if (drawFull) {
                    RenderUtil.drawRectWithTexture(BrowserGraphics.RES, gfx, x + borderThickness, y + borderThickness, 0, imageU, imageV, componentWidth - borderThickness * 2, componentHeight - borderThickness * 2, 256, 256);
                } else {
                    RenderUtil.drawRectWithTexture(BrowserGraphics.RES, gfx, x + borderThickness, y + borderThickness, imageU, imageV, componentWidth - borderThickness * 2, componentHeight - borderThickness * 2, imageWidth, imageHeight, sourceWidth, sourceHeight);
                }
            } else {
                gfx.fill(x + borderThickness, y + borderThickness, x + componentWidth - borderThickness, y + componentHeight - borderThickness, 0xff000000);
            }
            RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        } else {
            gfx.fill(x, y, x + componentWidth, y + componentHeight, 0xff000000);
        }
    }

    private int _pBorderThickness = 1;

    /**
     * Makes it so the border shows
     *
     * @param show should the border show
     */
    public void setBorderVisible(boolean show) {
        this.hasBorder = show;
        this.borderThickness = show ? _pBorderThickness : 0;
    }

    /**
     * Sets the border color for this component
     *
     * @param color the border color
     */
    private void setBorderColor(Color color) {
        this.borderColor = color.getRGB();
    }

    /**
     * Sets the thickness of the border
     *
     * @param thickness how thick in pixels
     */
    public void setBorderThickness(int thickness) {
        this._pBorderThickness = thickness;
        this.borderThickness = thickness;
    }

    public void setDrawFull(boolean drawFull) {
        this.drawFull = drawFull;
    }
}
