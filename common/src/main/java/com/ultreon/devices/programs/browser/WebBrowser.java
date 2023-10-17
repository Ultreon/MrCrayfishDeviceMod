package com.ultreon.devices.programs.browser;

import com.mojang.blaze3d.platform.Window;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.component.BrowserRenderer;
import com.ultreon.devices.cef.BrowserFramework;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.util.GLHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.*;

public class WebBrowser extends Application {
    private final int browserWidth;
    private final int browserHeight;
    private int lastMouseX = Integer.MAX_VALUE;
    private int lastMouseY = Integer.MAX_VALUE;
    private int x;
    private int y;
    private boolean active = false;

    public WebBrowser() {
        this.browserWidth = 362;
        this.browserHeight = 165;

        this.setDefaultWidth(browserWidth);
        this.setDefaultHeight(browserHeight);
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        addComponent(new BrowserRenderer(0, 0, browserWidth, browserHeight, 0, 0, browserWidth, browserHeight, browserWidth, browserHeight));
    }

    @Override
    public void onTick() {
        super.onTick();
        
        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        BrowserFramework.getUi().setSize((int) (browserWidth * guiScale), (int) (browserHeight * guiScale));
        BrowserFramework.getUi().setPreferredSize(new Dimension((int) (browserWidth * guiScale), (int) (browserHeight * guiScale)));
        BrowserFramework.getUi().setMaximumSize(new Dimension((int) (browserWidth * guiScale), (int) (browserHeight * guiScale)));
        BrowserFramework.getUi().setMaximumSize(new Dimension((int) (browserWidth * guiScale), (int) (browserHeight * guiScale)));
//        if (BrowserFramework.getUi().getWidth() > 0 && BrowserFramework.getUi().getHeight() > 0) {
            BrowserFramework.redraw();
//        }
    }

    @Override
    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
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

        for (MouseListener mouseMotionListener : BrowserFramework.getUi().getMouseListeners()) {
            mouseMotionListener.mousePressed(new MouseEvent(BrowserFramework.getUi(), MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, mx, my, 1, false, mb));
            mouseMotionListener.mouseClicked(new MouseEvent(BrowserFramework.getUi(), MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, mx, my, 1, false, mb));
        }
    }

    @Override
    public void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {
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

        for (MouseMotionListener mouseMotionListener : BrowserFramework.getUi().getMouseMotionListeners()) {
            mouseMotionListener.mouseDragged(new MouseEvent(BrowserFramework.getUi(), MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, mx, my, 1, false, mb));
        }
    }

    @Override
    public void handleMouseRelease(int mouseX, int mouseY, int mouseButton) {
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

        for (MouseListener mouseMotionListener : BrowserFramework.getUi().getMouseListeners()) {
            mouseMotionListener.mouseReleased(new MouseEvent(BrowserFramework.getUi(), MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, mx, my, 1, false, mb));
        }
    }

    @Override
    public void handleMouseScroll(int mouseX, int mouseY, double delta, boolean direction) {
        super.handleMouseScroll(mouseX, mouseY, delta, direction);

        double guiScale = Minecraft.getInstance().getWindow().getGuiScale();
        int mx = (int) ((mouseX - x) * guiScale);
        int my = (int) ((mouseY - y) * guiScale);

        for (MouseWheelListener mouseMotionListener : BrowserFramework.getUi().getMouseWheelListeners()) {
            System.out.println("Scroll Event");
            mouseMotionListener.mouseWheelMoved(new MouseWheelEvent(BrowserFramework.getUi(), MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, mx, my, mx, my, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, (int)delta, (int)delta, delta));
        }
    }

    @Override
    public void handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        super.handleKeyPressed(keyCode, scanCode, modifiers);

        if (this.active) {
            for (KeyListener mouseMotionListener : BrowserFramework.getUi().getKeyListeners()) {
                System.out.println("Key Pressed Event");
                if (keyCode >= 32 && keyCode <= 127) {
                    mouseMotionListener.keyPressed(new KeyEvent(BrowserFramework.getUi(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), modifiers, keyCode, (char)0, KeyEvent.KEY_LOCATION_UNKNOWN));
                }
            }
        }
    }

    @Override
    public void handleKeyReleased(int keyCode, int scanCode, int modifiers) {
        super.handleKeyReleased(keyCode, scanCode, modifiers);

        if (this.active) {
            for (KeyListener mouseMotionListener : BrowserFramework.getUi().getKeyListeners()) {
                System.out.println("Key Released Event");
                if (keyCode >= 32 && keyCode <= 127) {
                    mouseMotionListener.keyReleased(new KeyEvent(BrowserFramework.getUi(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), modifiers, keyCode, (char)0, KeyEvent.KEY_LOCATION_UNKNOWN));
                }
            }
        }
    }

    @Override
    public void handleCharTyped(char character, int modifiers) {
        super.handleCharTyped(character, modifiers);

        if (this.active) {
            for (KeyListener mouseMotionListener : BrowserFramework.getUi().getKeyListeners()) {
                System.out.println("Key Typed Event");
                mouseMotionListener.keyTyped(new KeyEvent(BrowserFramework.getUi(), KeyEvent.KEY_TYPED, System.currentTimeMillis(), modifiers, KeyEvent.VK_UNDEFINED, character, KeyEvent.KEY_LOCATION_UNKNOWN));
            }
        }
    }

    @Override
    public void render(GuiGraphics gfx, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {
        GLHelper.pushScissor(x, y, getWidth(), getHeight());
        this.x = x;
        this.y = y;
        this.active = active;
        Window window = Minecraft.getInstance().getWindow();
        double guiScale = window.getGuiScale();
        if (mouseX != lastMouseX || mouseY != lastMouseY && active) {
            int mx = (int) ((mouseX - x) * guiScale);
            int my = (int) ((mouseY - y) * guiScale);
            for (MouseMotionListener mouseMotionListener : BrowserFramework.getUi().getMouseMotionListeners()) {
                mouseMotionListener.mouseMoved(new MouseEvent(BrowserFramework.getUi(), MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, mx, my, 0, false));
            }

            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }

        BrowserFramework.getUi().setSize((int) (browserWidth * guiScale), (int) (browserHeight * guiScale));
        BrowserFramework.getUi().setPreferredSize(new Dimension((int) (browserWidth * guiScale), (int) (browserHeight * guiScale)));
        BrowserFramework.getUi().setMaximumSize(new Dimension((int) (browserWidth * guiScale), (int) (browserHeight * guiScale)));
        BrowserFramework.getUi().setMaximumSize(new Dimension((int) (browserWidth * guiScale), (int) (browserHeight * guiScale)));
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
