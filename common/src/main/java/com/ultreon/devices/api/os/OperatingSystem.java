package com.ultreon.devices.api.os;

import com.ultreon.devices.client.Display;
import com.ultreon.devices.object.AppInfo;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import com.ultreon.devices.api.bios.Bios;
import com.ultreon.devices.api.bios.InterruptData;
import net.minecraft.client.gui.GuiGraphics;
import com.ultreon.devices.api.app.Application;

public interface OperatingSystem {
    void init(GuiGraphics graphics);

    void boot(Bios bios);

    void render(GuiGraphics display);

    void onShutdownRequest(ShutdownSource source);

    boolean onBiosInterrupt(InterruptData interrupt);

    boolean mouseReleased(double mouseX, double mouseY, int state);

    boolean mouseReleased(int mouseX, int mouseY, int state);

    void afterKeyboardAction();

    boolean charTyped(char codePoint, int modifiers);

    boolean keyPressed(int keyCode, int scanCode, int modifiers);

    Application openApplication(AppInfo info);

    Application openApplication(AppInfo info, CompoundTag intentTag);

    boolean isWorldLess();

    Screen getScreen();

    void connectDisplay(Display display);

    void disconnectDisplay();
}
