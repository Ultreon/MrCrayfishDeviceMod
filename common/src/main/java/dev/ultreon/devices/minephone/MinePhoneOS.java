package dev.ultreon.devices.minephone;

import dev.ultreon.devices.api.bios.Bios;
import dev.ultreon.devices.api.os.OSScreen;
import dev.ultreon.devices.api.video.CustomResolution;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.client.DisplayGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class MinePhoneOS extends OSScreen {
    private final ComputerBlockEntity computer;
    private final Bios bios;
    private final PhoneBootScreen bootDisplay = new PhoneBootScreen();
    private boolean booted;

    public MinePhoneOS(ComputerBlockEntity computer, Bios bios) {
        super();
        this.computer = computer;
        this.bios = bios;

        this.boot(bios);
    }

    @Override
    public void boot(Bios bios) {
        CompletableFuture.runAsync(() -> {
            // Simulate booting
            try {
                do {
                    bootDisplay.progress++;
                    Thread.sleep(100);
                } while (bootDisplay.progress < bootDisplay.maxProgress);

                this.booted = true;
            } catch (Exception e) {
                this.bsod(e);
            }
        });
    }

    private void bsod(Exception e) {

    }

    @Override
    public void render(GuiGraphics display) {
        display.fill(0, 0, width, height, 0xff000000);
    }

    @Override
    public void afterKeyboardAction() {

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public Screen getScreen() {
        return this;
    }

    @Override
    public void connectDisplay(DisplayGui display) {
        display.setResolution(new CustomResolution(116, 216));

        super.connectDisplay(display);
    }

    @Override
    public void disconnectDisplay() {
        bios.powerOff();

        super.disconnectDisplay();
    }

    @Override
    public CompoundTag writeState() {
        return new CompoundTag();
    }

    public ComputerBlockEntity getComputer() {
        return computer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MinePhoneOS) obj;
        return Objects.equals(this.computer, that.computer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(computer);
    }

    @Override
    public String toString() {
        return "MinePhoneOS[" +
                "computer=" + computer + ']';
    }

}
