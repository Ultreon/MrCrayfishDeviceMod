package dev.ultreon.devices.api.os;

import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.bios.Bios;
import dev.ultreon.devices.api.bios.InterruptData;
import dev.ultreon.devices.client.Display;
import dev.ultreon.devices.object.AppInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;

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
