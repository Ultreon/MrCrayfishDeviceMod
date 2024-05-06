package dev.ultreon.devices.minephone;

import net.minecraft.client.gui.GuiGraphics;

public interface PosRenderable {

    void render(GuiGraphics guiGraphics, MinePhoneOS phoneOS, int mouseX, int mouseY, float partialTick);
}
