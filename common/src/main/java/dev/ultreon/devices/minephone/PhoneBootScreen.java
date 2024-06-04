package dev.ultreon.devices.minephone;

import dev.ultreon.devices.UltreonDevicesMod;
import dev.ultreon.devices.client.DisplayGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class PhoneBootScreen implements PosRenderable {
    private static final ResourceLocation PHONE_GUI = UltreonDevicesMod.res("textures/gui/minephone.png");
    int progress = 0;
    final int maxProgress = 100;

    @Override
    public void render(GuiGraphics guiGraphics, MinePhoneOS phoneOS, int mouseX, int mouseY, float partialTick) {
        DisplayGui display = phoneOS.getDisplay();

        int screenWidth = display.getScreenWidth();
        int screenHeight = display.getScreenHeight();

        guiGraphics.blit(PHONE_GUI, screenWidth / 2 - 50, screenHeight - 40, 0, 0, 32, 32, 100, 20);

        guiGraphics.fill(0, 0, screenWidth, screenHeight, 0xff000000);
        guiGraphics.fill(screenWidth / 2 - 50, screenHeight - 40, screenWidth / 2 + 50, screenHeight - 38, 0xffffffff);
    }
}
