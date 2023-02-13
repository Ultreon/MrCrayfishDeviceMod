package com.ultreon.devices.cef;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class CEFLicenseScreen extends Screen {
    private static final Component TITLE = Component.translatable("screen.devices.cef.license.title");
    private static final Component DESCRIPTION = Component.translatable("screen.devices.cef.license.description");
    private final Screen back;
    private CEFLicenseWidget licenseWidget;

    public CEFLicenseScreen(Screen back) {
        super(TITLE);
        this.back = back;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.licenseWidget = addRenderableWidget(new CEFLicenseWidget(10, 60, width - 20, height - 100, Component.empty()));
        addRenderableWidget(Button.builder(Component.translatable("button.devices.cef.license.agree"), button -> {
            assert minecraft != null;
            minecraft.setScreen(new CEFDownloadScreen(back));
        }).bounds(width / 2 + 5, height - 30, 80, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("button.devices.cef.license.decline"), button -> {
            assert minecraft != null;
            minecraft.stop();
        }).bounds(width / 2 - 85, height - 30, 80, 20).build());
    }

    public CEFLicenseWidget getLicenseWidget() {
        return licenseWidget;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float frameTime) {
        renderBackground(poseStack);
        poseStack.pushPose();
        {
            poseStack.scale(2, 2, 1);
            drawCenteredString(poseStack, font, title, width / 4, 10 / 2, 0xffffff);
        }
        poseStack.popPose();

        drawCenteredString(poseStack, font, DESCRIPTION, width / 2, 35, 0xffffff);

        super.render(poseStack, mouseX, mouseY, frameTime);
    }
}
