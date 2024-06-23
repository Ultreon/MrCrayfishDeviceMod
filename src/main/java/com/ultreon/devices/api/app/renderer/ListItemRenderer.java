package com.ultreon.devices.api.app.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

public abstract class ListItemRenderer<E> {
    private final int height;

    public ListItemRenderer(int height) {
        this.height = height;
    }

    public final int getHeight() {
        return height;
    }

    public abstract void render(MatrixStack pose, E e, AbstractGui gui, Minecraft mc, int x, int y, int width, int height, boolean selected);
}
