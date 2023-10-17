package com.ultreon.devices.cef;

import com.ultreon.devices.Devices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class CEFLicenseWidget extends AbstractScrollWidget {
    private final String license;
    private final Font font = Minecraft.getInstance().font;

    {
        try (final var licenseStream = Devices.class.getResourceAsStream("/cef-license.txt")) {
            if (licenseStream == null) {
                throw new Error("CEF License not found in mod jar-resources.");
            }
            license = new String(licenseStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CEFLicenseWidget(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    public String getLicense() {
        return license;
    }

    @Override
    protected int getInnerHeight() {
        Objects.requireNonNull(this.font);
        return 9 * license.lines().toList().size() + 8;
    }

    @Override
    protected boolean scrollbarVisible() {
        return true;
    }

    @Override
    protected double scrollRate() {
        Objects.requireNonNull(this.font);
        return 9.0;
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics gfx, int i, int j, float f) {
        List<String> list = license.lines().toList();
        for (int lineNr = 0, listSize = list.size(); lineNr < listSize; lineNr++) {
            String line = list.get(lineNr);
            int offset = lineNr * 9;
            gfx.drawString(this.font, line, getX() + 4, getY() + 4 + offset, 0xffffff, true);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}
