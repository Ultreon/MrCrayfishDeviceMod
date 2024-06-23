package com.ultreon.devices.api.app.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;

/**
 * @author MrCrayfish
 */
public abstract class ItemRenderer<E> {
    public abstract void render(MatrixStack pose, E e, AbstractGui gui, Minecraft mc, int x, int y, int width, int height);
}
