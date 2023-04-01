package com.ultreon.devices.core.laptop.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.Reference;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public class ClientLaptopScreen extends Screen {
    static final ResourceLocation LAPTOP_GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/laptop.png");
    private static final int BORDER = 10;
    private final ClientLaptop laptop;


    public ClientLaptopScreen(ClientLaptop laptop) {
        super(Component.translatable(laptop.toString()));
        this.laptop = laptop;
    }

    public void renderBezels(final @NotNull PoseStack pose, final int mouseX, final int mouseY, float partialTicks) {
        this.renderBackground(pose);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, LAPTOP_GUI);

        //*************************//
        //     Physical Screen     //
        //*************************//
        int posX = (width - ClientLaptop.DEVICE_WIDTH) / 2;
        int posY = (height - ClientLaptop.DEVICE_HEIGHT) / 2;

        // Corners
        blit(pose, posX, posY, 0, 0, BORDER, BORDER); // TOP-LEFT
        blit(pose, posX + ClientLaptop.DEVICE_WIDTH - BORDER, posY, 11, 0, BORDER, BORDER); // TOP-RIGHT
        blit(pose, posX + ClientLaptop.DEVICE_WIDTH - BORDER, posY + ClientLaptop.DEVICE_HEIGHT - BORDER, 11, 11, BORDER, BORDER); // BOTTOM-RIGHT
        blit(pose, posX, posY + ClientLaptop.DEVICE_HEIGHT - BORDER, 0, 11, BORDER, BORDER); // BOTTOM-LEFT

        // Edges
        Gui.blit(pose, posX + BORDER, posY, ClientLaptop.SCREEN_WIDTH, BORDER, 10, 0, 1, BORDER, 256, 256); // TOP
        Gui.blit(pose, posX + ClientLaptop.DEVICE_WIDTH - BORDER, posY + BORDER, BORDER, ClientLaptop.SCREEN_HEIGHT, 11, 10, BORDER, 1, 256, 256); // RIGHT
        Gui.blit(pose, posX + BORDER, posY + ClientLaptop.DEVICE_HEIGHT - BORDER, ClientLaptop.SCREEN_WIDTH, BORDER, 10, 11, 1, BORDER, 256, 256); // BOTTOM
        Gui.blit(pose, posX, posY + BORDER, BORDER, ClientLaptop.SCREEN_HEIGHT, 0, 11, BORDER, 1, 256, 256); // LEFT

        // Center
        Gui.blit(pose, posX + BORDER, posY + BORDER, ClientLaptop.SCREEN_WIDTH, ClientLaptop.SCREEN_HEIGHT, 10, 10, 1, 1, 256, 256);

    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int posX = (width - ClientLaptop.DEVICE_WIDTH) / 2 + BORDER;
        int posY = (height - ClientLaptop.DEVICE_HEIGHT) / 2 + BORDER;
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderBezels(poseStack, mouseX, mouseY, partialTick);
        poseStack.translate(posX, posY, 0);
        laptop.render(poseStack, mouseX-posX, mouseY-posY, partialTick);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        int posX = (width - ClientLaptop.DEVICE_WIDTH) / 2 + BORDER;
        int posY = (height - ClientLaptop.DEVICE_HEIGHT) / 2 + BORDER;
        super.mouseMoved(mouseX, mouseY);
        laptop.mouseMoved(mouseX-posX, mouseY-posY);
        System.out.println(Arrays.toString(laptop.square));
    }
}
