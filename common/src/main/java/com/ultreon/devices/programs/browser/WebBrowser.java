package com.ultreon.devices.programs.browser;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.cef.BrowserFramework;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.util.GLHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import org.cef.browser.CefBrowser;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CompletableFuture;

public class WebBrowser extends Application {
    private final int browserWidth;
    private final int browserHeight;
    private int lastMouseX = Integer.MAX_VALUE;
    private int lastMouseY = Integer.MAX_VALUE;
    private int x;
    private int y;
    private boolean active = false;
    private CefBrowser browser;
    private Component ui;
    private BrowserRenderer renderer;

    public WebBrowser() {
        this.browserWidth = 362;
        this.browserHeight = 165;

        this.setDefaultWidth(browserWidth);
        this.setDefaultHeight(browserHeight);
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        CompletableFuture.runAsync(() -> {
            browser = BrowserFramework.createBrowser();
            ui = browser.getUIComponent();

            RenderSystem.recordRenderCall(() -> {
                addComponent(renderer = new BrowserRenderer(
                        browser, ui, 0, 0, browserWidth, browserHeight, 0, 0,
                        browserWidth, browserHeight, browserWidth, browserHeight
                ));

                renderer.setVisible(true);
            });
        });
    }

    @Override
    public void onTick() {
        super.onTick();

        BrowserFramework.redraw();
    }

    @Override
    public void onClose() {
        BrowserFramework.closeBrowser(browser);
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (ui == null || browser == null) {
            return;
        }

        super.handleMouseClick(mouseX, mouseY, mouseButton);

        System.out.println("Click Event");

        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        int mx = (int) ((mouseX - x) * guiScale);
        int my = (int) ((mouseY - y) * guiScale);
        int mb = switch (mouseButton) {
            case 0 -> 1;
            case 1 -> 3;
            case 2 -> 2;
            case 3 -> 4;
            case 4 -> 5;
            default -> 0;
        };

        for (MouseListener mouseMotionListener : ui.getMouseListeners()) {
            mouseMotionListener.mousePressed(new MouseEvent(ui, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, mx, my, 1, false, mb));
            mouseMotionListener.mouseClicked(new MouseEvent(ui, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, mx, my, 1, false, mb));
        }
    }

    @Override
    public void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {
        if (ui == null || browser == null) {
            return;
        }

        super.handleMouseDrag(mouseX, mouseY, mouseButton);

        System.out.println("Drag Event");

        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        int mx = (int) ((mouseX - x) * guiScale);
        int my = (int) ((mouseY - y) * guiScale);
        int mb = switch (mouseButton) {
            case 0 -> 1;
            case 1 -> 3;
            case 2 -> 2;
            case 3 -> 4;
            case 4 -> 5;
            default -> 0;
        };

        for (MouseMotionListener mouseMotionListener : ui.getMouseMotionListeners()) {
            mouseMotionListener.mouseDragged(new MouseEvent(ui, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, mx, my, 1, false, mb));
        }
    }

    @Override
    public void handleMouseRelease(int mouseX, int mouseY, int mouseButton) {
        if (ui == null || browser == null) {
            return;
        }

        super.handleMouseRelease(mouseX, mouseY, mouseButton);

        System.out.println("Release Mouse Event");

        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        int mx = (int) ((mouseX - x) * guiScale);
        int my = (int) ((mouseY - y) * guiScale);
        int mb = switch (mouseButton) {
            case 0 -> 1;
            case 1 -> 3;
            case 2 -> 2;
            case 3 -> 4;
            case 4 -> 5;
            default -> 0;
        };

        for (MouseListener mouseMotionListener : ui.getMouseListeners()) {
            mouseMotionListener.mouseReleased(new MouseEvent(ui, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, mx, my, 1, false, mb));
        }
    }

    @Override
    public void handleMouseScroll(int mouseX, int mouseY, double delta, boolean direction) {
        if (ui == null || browser == null) {
            return;
        }

        super.handleMouseScroll(mouseX, mouseY, delta, direction);

        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        int mx = (int) ((mouseX - x) * guiScale);
        int my = (int) ((mouseY - y) * guiScale);

        for (MouseWheelListener mouseMotionListener : ui.getMouseWheelListeners()) {
            System.out.println("Scroll Event");
            mouseMotionListener.mouseWheelMoved(new MouseWheelEvent(ui, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, mx, my, mx, my, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, (int) delta, (int) delta, delta));
        }
    }

    @Override
    public void handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (ui == null || browser == null) {
            return;
        }

        super.handleKeyPressed(keyCode, scanCode, modifiers);

        if (this.active) {
            for (KeyListener mouseMotionListener : ui.getKeyListeners()) {
                System.out.println("Key Pressed Event");
                if (keyCode >= 32 && keyCode <= 127) {
                    mouseMotionListener.keyPressed(new KeyEvent(ui, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), modifiers, keyCode, (char) 0, KeyEvent.KEY_LOCATION_UNKNOWN));
                }
            }
        }
    }

    @Override
    public void handleKeyReleased(int keyCode, int scanCode, int modifiers) {
        if (ui == null || browser == null) {
            return;
        }

        super.handleKeyReleased(keyCode, scanCode, modifiers);

        if (this.active) {
            for (KeyListener mouseMotionListener : ui.getKeyListeners()) {
                System.out.println("Key Released Event");
                if (keyCode >= 32 && keyCode <= 127) {
                    mouseMotionListener.keyReleased(new KeyEvent(ui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), modifiers, keyCode, (char) 0, KeyEvent.KEY_LOCATION_UNKNOWN));
                }
            }
        }
    }

    @Override
    public void handleCharTyped(char character, int modifiers) {
        if (ui == null || browser == null) {
            return;
        }

        super.handleCharTyped(character, modifiers);

        if (this.active) {
            for (KeyListener mouseMotionListener : ui.getKeyListeners()) {
                System.out.println("Key Typed Event");
                mouseMotionListener.keyTyped(new KeyEvent(ui, KeyEvent.KEY_TYPED, System.currentTimeMillis(), modifiers, KeyEvent.VK_UNDEFINED, character, KeyEvent.KEY_LOCATION_UNKNOWN));
            }
        }
    }

    @Override
    public void render(GuiGraphics gfx, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {
        if (this.ui == null || this.browser == null) {
            return;
        }

        if (laptop.isFocusedWindow(this)) {
            this.active = true;
        }

        GLHelper.pushScissor(x, y, getWidth(), getHeight());
        this.x = x;
        this.y = y;
        this.active = active;
        Window window = Minecraft.getInstance().getWindow();
        double guiScale = window.getGuiScale();
        if (mouseX != lastMouseX || mouseY != lastMouseY && active) {
            int mx = (int) ((mouseX - x) * guiScale);
            int my = (int) ((mouseY - y) * guiScale);

            for (MouseMotionListener mouseMotionListener : ui.getMouseMotionListeners()) {
                mouseMotionListener.mouseMoved(new MouseEvent(ui, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, mx, my, 0, false));
            }

            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }

        BrowserFramework.setSize(browser, getWidth(), getHeight());
        BrowserFramework.renderBrowser(gfx, x, y, getWidth(), getHeight());
        GLHelper.popScissor();
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }
}
