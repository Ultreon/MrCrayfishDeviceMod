package com.ultreon.devices.programs.browser;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.ultreon.devices.Devices;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.programs.system.layout.StandardLayout;
import com.ultreon.devices.util.GLHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class WebBrowser extends Application {
    private int browserWidth;
    private int browserHeight;
    private MCEFBrowser browser;
    private StandardLayout layoutBrowser;
    private boolean init;

    public WebBrowser() {

    }

    @Override
    public synchronized void init(@Nullable CompoundTag intent) {
        if (browser != null && !init) {
            this.resizeBrowser();
            return;
        }

        this.init = true;

        this.layoutBrowser = new StandardLayout(null, 362, 165 + 75, this, null);
        this.setCurrentLayout(this.layoutBrowser);

        this.setBrowserWidth(362);
        this.setBrowserHeight(165);

        RenderSystem.recordRenderCall(() -> {
            browser = MCEF.createBrowser("https://www.google.com", true, getWidth(), getHeight());
            this.resizeBrowser();
        });

        Devices.LOGGER.warn("Browser initialized");
    }

    private void resizeBrowser() {
        if (this.browser == null) return;

        if (getBrowserWidth() > 100 && getBrowserHeight() > 100) {
            this.browser.resize(this.scaleX(this.getBrowserWidth()), this.scaleY(this.getBrowserHeight()));
        }
    }

    private int mouseX(double x) {
        return (int)((x - this.getCurrentLayout().xPosition) * this.minecraft().getWindow().getGuiScale());
    }

    private int mouseY(double y) {
        return (int)((y - this.getCurrentLayout().yPosition) * this.minecraft().getWindow().getGuiScale());
    }

    private int scaleX(double x) {
        return (int)((x) * this.minecraft().getWindow().getGuiScale());
    }

    private int scaleY(double y) {
        return (int)((y) * this.minecraft().getWindow().getGuiScale());
    }

    private Minecraft minecraft() {
        return Minecraft.getInstance();
    }

    @Override
    public void onTick() {
        super.onTick();
    }

    @Override
    public void onClose() {
        this.browser.close();
        this.browser = null;

        super.onClose();
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int button) {
        if (this.browser == null) return;

        this.browser.sendMousePress(this.mouseX(mouseX), this.mouseY(mouseY), button);
        this.browser.setFocus(true);
        super.handleMouseClick(mouseX, mouseY, button);
    }

    @Override
    public void handleMouseRelease(int mouseX, int mouseY, int button) {
        if (this.browser == null) return;

        this.browser.sendMouseRelease(this.mouseX(mouseX), this.mouseY(mouseY), button);
        this.browser.setFocus(true);
        super.handleMouseRelease(mouseX, mouseY, button);
    }

    @Override
    public void handleMouseMove(int mouseX, int mouseY) {
        if (this.browser == null) return;

        this.browser.sendMouseMove(this.mouseX(mouseX), this.mouseY(mouseY));
        super.handleMouseMove(mouseX, mouseY);
    }

    @Override
    public void handleMouseDrag(int mouseX, int mouseY, int button) {
        if (this.browser == null) return;

        super.handleMouseDrag(mouseX, mouseY, button);
    }

    @Override
    public void handleMouseScroll(int mouseX, int mouseY, double delta, boolean direction) {
        if (this.browser == null) return;

        this.browser.sendMouseWheel(this.mouseX(mouseX), this.mouseY(mouseY), delta, 0);
        super.handleMouseScroll(mouseX, mouseY, delta, direction);
    }

    @Override
    public void handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.browser == null) return;

        this.browser.sendKeyPress(keyCode, scanCode, modifiers);
        this.browser.setFocus(true);
        super.handleKeyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void handleKeyReleased(int keyCode, int scanCode, int modifiers) {
        if (this.browser == null) return;

        this.browser.sendKeyRelease(keyCode, scanCode, modifiers);
        this.browser.setFocus(true);
        super.handleKeyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void handleCharTyped(char codePoint, int modifiers) {
        if (this.browser == null || codePoint == 0) return;

        this.browser.sendKeyTyped(codePoint, modifiers);
        this.browser.setFocus(true);
        super.handleCharTyped(codePoint, modifiers);
    }
    
    @Override
    public void render(GuiGraphics gfx, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {
        if (this.browser == null) return;

        laptop.isFocusedWindow(this);

        GLHelper.pushScissor(x, y, getBrowserWidth(), getBrowserHeight());
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, this.browser.getRenderer().getTextureID());

        Tesselator t = Tesselator.getInstance();
        BufferBuilder buffer = t.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(x, this.getBrowserHeight() + y, 0.0).uv(0.0F, 1.0F).color(255, 255, 255, 255).endVertex();
        buffer.vertex(this.getBrowserWidth() + x, this.getBrowserHeight() + y, 0.0).uv(1.0F, 1.0F).color(255, 255, 255, 255).endVertex();
        buffer.vertex(this.getBrowserWidth() + x, y, 0.0).uv(1.0F, 0.0F).color(255, 255, 255, 255).endVertex();
        buffer.vertex(x, y, 0.0).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
        t.end();
        
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
        GLHelper.popScissor();
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }

    public int getBrowserWidth() {
        return browserWidth;
    }

    void setBrowserWidth(int browserWidth) {
        this.browserWidth = browserWidth;
        this.layoutBrowser.width = browserWidth;
    }

    public int getBrowserHeight() {
        return browserHeight;
    }

    void setBrowserHeight(int browserHeight) {
        this.browserHeight = browserHeight;
        this.layoutBrowser.height = browserHeight + 75;
    }

    public void resize(int width, int height) {
        this.browserWidth = width;
        this.browserHeight = height;
        this.layoutBrowser.width = width;
        this.layoutBrowser.height = height + 75;

        this.resizeBrowser();
    }
}
