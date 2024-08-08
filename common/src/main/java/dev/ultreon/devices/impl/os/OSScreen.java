package dev.ultreon.devices.impl.os;

import dev.ultreon.devices.api.os.OperatingSystem;
import dev.ultreon.devices.api.os.ShutdownSource;
import dev.ultreon.mineos.api.Application;
import dev.ultreon.devices.impl.bios.Bios;
import dev.ultreon.devices.impl.bios.InterruptData;
import dev.ultreon.devices.client.Display;
import dev.ultreon.mineos.object.AppInfo;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

@Deprecated(forRemoval = true)
public abstract class OSScreen extends Screen implements OperatingSystem {
    private Display display;

    protected OSScreen() {
        super(Component.literal("Virtual Display"));
    }

    @Override
    public void init(GuiGraphics graphics) {

    }

    @Override
    public void boot(Bios bios) {

    }

    @Override
    public void render(GuiGraphics display) {

    }

    @Override
    public void onShutdownRequest(ShutdownSource source) {

    }

    @Override
    public boolean onBiosInterrupt(InterruptData interrupt) {
        return false;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int state) {
        return false;
    }

    @Override
    public Application openApplication(AppInfo info) {
        return null;
    }

    @Override
    public Application openApplication(AppInfo info, CompoundTag intentTag) {
        return null;
    }

    @Override
    public boolean isWorldLess() {
        return false;
    }

    @Override
    public Screen getScreen() {
        return null;
    }

    @Override
    public void connectDisplay(Display display) {
        this.display = display;
        this.width = display.getScreenWidth();
        this.height = display.getScreenHeight();
    }

    @Override
    public void disconnectDisplay() {
        this.display = null;
    }

    public Display getDisplay() {
        return display;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }
}
