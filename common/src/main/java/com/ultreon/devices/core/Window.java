package com.ultreon.devices.core;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.gui.GuiButtonClose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;

public class Window<T extends Wrappable> {
    public static final ResourceLocation WINDOW_GUI = new ResourceLocation("devices:textures/gui/application.png");

    public static final int Color_WINDOW_DARK = new Color(0f, 0f, 0f, 0.25f).getRGB();
    final Laptop laptop;
    protected GuiButtonClose btnClose;
    T content;
    int width, height;
    int offsetX, offsetY;
    Window<Dialog> dialogWindow = null;
    Window<? extends Wrappable> parent = null;
    protected boolean removed;

    public Window(T wrappable, Laptop laptop) {
        this.content = wrappable;
        this.laptop = laptop;
        wrappable.setWindow(this);
    }

    void setWidth(int width) {
        this.width = width + 2;
        if (this.width > Laptop.SCREEN_WIDTH) {
            this.width = Laptop.SCREEN_WIDTH;
        }
    }

    void setHeight(int height) {
        this.height = height + 14;
        if (this.height > 178) {
            this.height = 178;
        }
    }

    void init(int x, int y, @Nullable CompoundNBT intent) {
        try {
            btnClose = new GuiButtonClose(x + offsetX + width - 12, y + offsetY + 1);
            content.init(intent);
        } catch (Exception e) {
            e.printStackTrace();

            Window.this.close();
            Dialog.Message message = new Dialog.Message("Error initializing window:\n" + e.getMessage()) {
                @Override
                public void onClose() {
                    super.onClose();
                }
            };

            closeDialog();
            openDialog(message);
        }
    }

    public void onTick() {
        if (dialogWindow != null) {
            dialogWindow.onTick();
        }
        content.onTick();
    }

    public void render(MatrixStack pose, Laptop gui, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {
        if (content.isPendingLayoutUpdate()) {
            this.setWidth(content.getWidth());
            this.setHeight(content.getHeight());
            this.offsetX = (Laptop.SCREEN_WIDTH - width) / 2;
            this.offsetY = (Laptop.SCREEN_HEIGHT - TaskBar.BAR_HEIGHT - height) / 2;
            updateComponents(x, y);
            content.clearPendingLayout();
        }

        pose.pushPose();

        RenderSystem.blendColor(1f, 1f, 1f, 1f);
        RenderSystem.enableBlend();
        mc.textureManager.bind(WINDOW_GUI);

        /* Corners */
        gui.blit(pose, x + offsetX, y + offsetY, 0, 0, 1, 13);
        gui.blit(pose, x + offsetX + width - 13, y + offsetY, 2, 0, 13, 13);
        gui.blit(pose, x + offsetX + width - 1, y + offsetY + height - 1, 14, 14, 1, 1);
        gui.blit(pose, x + offsetX, y + offsetY + height - 1, 0, 14, 1, 1);

        /* Edges */
        AbstractGui.blit(pose, x + offsetX + 1, y + offsetY, width - 14, 13, 1, 0, 1, 13, 256, 256);
        AbstractGui.blit(pose, x + offsetX + width - 1, y + offsetY + 13, 1, height - 14, 14, 13, 1, 1, 256, 256);
        AbstractGui.blit(pose, x + offsetX + 1, y + offsetY + height - 1, width - 2, 1, 1, 14, 13, 1, 256, 256);
        AbstractGui.blit(pose, x + offsetX, y + offsetY + 13, 1, height - 14, 0, 13, 1, 1, 256, 256);

        /* Center */
        AbstractGui.blit(pose, x + offsetX + 1, y + offsetY + 13, width - 2, height - 14, 1, 13, 13, 1, 256, 256);

        String windowTitle = content.getWindowTitle();
        if (mc.font.width(windowTitle) > width - 2 - 13 - 3) { // window width, border, close button, padding, padding
            windowTitle = mc.font.plainSubstrByWidth(windowTitle, width - 2 - 13 - 3);
        }
        mc.font.drawShadow(pose, windowTitle, x + offsetX + 3, y + offsetY + 3, Color.WHITE.getRGB(), true);

        btnClose.renderButton(pose, mouseX, mouseY, partialTicks);

        RenderSystem.disableBlend();

        /* Render content */
        content.render(pose, gui, mc, x + offsetX + 1, y + offsetY + 13, mouseX, mouseY, active && dialogWindow == null, partialTicks);

        RenderSystem.blendColor(1f, 1f, 1f, 1f);

        if (dialogWindow != null) {
            IngameGui.fill(pose, x + offsetX, y + offsetY, x + offsetX + width, y + offsetY + height, Color_WINDOW_DARK);
            dialogWindow.render(pose, gui, mc, x, y, mouseX, mouseY, active, partialTicks);
        }
        pose.popPose();
    }

    @Deprecated
    public void handleKeyTyped(char character, int code) {
        if (dialogWindow != null) {
            dialogWindow.handleKeyTyped(character, code);
            return;
        }
        content.handleKeyTyped(character, code);
    }

    @Deprecated
    public void handleKeyReleased(char character, int code) {
        if (dialogWindow != null) {
            dialogWindow.handleKeyReleased(character, code);
            return;
        }
        content.handleKeyReleased(character, code);
    }

    public void handleCharTyped(char codePoint, int modifiers) {
        if (dialogWindow != null) {
            dialogWindow.handleCharTyped(codePoint, modifiers);
            return;
        }
        content.handleCharTyped(codePoint, modifiers);
    }

    public void handleKeyPressed(int keyCode, int scanCode, int modifiers) {
        if (dialogWindow != null) {
            dialogWindow.handleKeyPressed(keyCode, scanCode, modifiers);
            return;
        }
        content.handleKeyPressed(keyCode, scanCode, modifiers);
    }

    public void handleKeyReleased(int keyCode, int scanCode, int modifiers) {
        if (dialogWindow != null) {
            dialogWindow.handleKeyReleased(keyCode, scanCode, modifiers);
            return;
        }
        content.handleKeyReleased(keyCode, scanCode, modifiers);
    }

    public void handleWindowMove(int screenStartX, int screenStartY, int mouseDX, int mouseDY) {
        int newX = offsetX + mouseDX;
        int newY = offsetY + mouseDY;

        if (newX >= 0 && newX <= Laptop.SCREEN_WIDTH - width) {
            this.offsetX = newX;
        } else if (newX < 0) {
            this.offsetX = 0;
        } else {
            this.offsetX = Laptop.SCREEN_WIDTH - width;
        }

        if (newY >= 0 && newY <= Laptop.SCREEN_HEIGHT - TaskBar.BAR_HEIGHT - height) {
            this.offsetY = newY;
        } else if (newY < 0) {
            this.offsetY = 0;
        } else {
            this.offsetY = Laptop.SCREEN_HEIGHT - TaskBar.BAR_HEIGHT - height;
        }

        updateComponents(screenStartX, screenStartY);
    }

    void handleMouseClick(Laptop gui, int x, int y, int mouseX, int mouseY, int mouseButton) {
        if (btnClose.isHovered()) {
            if (content instanceof Application) {
                gui.closeApplication(((Application) content).getInfo());
                return;
            }

            if (parent != null) {
                parent.closeDialog();
            }
        }

        if (dialogWindow != null) {
            dialogWindow.handleMouseClick(gui, x, y, mouseX, mouseY, mouseButton);
            return;
        }

        content.handleMouseClick(mouseX, mouseY, mouseButton);
    }

    void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {
        if (dialogWindow != null) {
            dialogWindow.handleMouseDrag(mouseX, mouseY, mouseButton);
            return;
        }
        content.handleMouseDrag(mouseX, mouseY, mouseButton);
    }

    void handleMouseRelease(int mouseX, int mouseY, int mouseButton) {
        if (dialogWindow != null) {
            dialogWindow.handleMouseRelease(mouseX, mouseY, mouseButton);
            return;
        }
        content.handleMouseRelease(mouseX, mouseY, mouseButton);
    }

    void handleMouseScroll(int mouseX, int mouseY, boolean direction) {
        if (dialogWindow != null) {
            dialogWindow.handleMouseScroll(mouseX, mouseY, direction);
            return;
        }
        content.handleMouseScroll(mouseX, mouseY, direction);
    }

    public void handleClose() {
        content.onClose();
    }

    private void updateComponents(int x, int y) {
        content.updateComponents(x + offsetX + 1, y + offsetY + 13);
        btnClose.x = x + offsetX + width - 12;
        btnClose.y = y + offsetY + 1;
    }

    public void openDialog(Dialog dialog) {
        if (dialogWindow != null) {
            dialogWindow.openDialog(dialog);
        } else {
            dialogWindow = new Window(dialog, null);
            dialogWindow.init(0, 0, null);
            dialogWindow.setParent(this);
        }
    }

    public void closeDialog() {
        if (dialogWindow != null) {
            dialogWindow.handleClose();
            dialogWindow = null;
        }
    }

    public Window<Dialog> getDialogWindow() {
        return dialogWindow;
    }

    public final void close() {
        this.removed = true;
        if (content instanceof Application) {
            laptop.closeApplication(((Application) content).getInfo());
            return;
        }
        if (parent != null) {
            parent.closeDialog();
        }
    }

    public Window getParent() {
        return parent;
    }

    public void setParent(Window parent) {
        this.parent = parent;
    }

    public T getContent() {
        return content;
    }
}
