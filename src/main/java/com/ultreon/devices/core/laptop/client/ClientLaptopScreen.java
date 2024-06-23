package com.ultreon.devices.core.laptop.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.Reference;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public class ClientLaptopScreen extends Screen {
    static final ResourceLocation LAPTOP_GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/laptop.png");
    private static final int BORDER = 10;
    private final ClientLaptop laptop;


    public ClientLaptopScreen(ClientLaptop laptop) {
        super(new TranslationTextComponent(laptop.toString()));
        this.laptop = laptop;
    }

    public void renderBezels(final @NotNull MatrixStack pose, final int mouseX, final int mouseY, float partialTicks) {
        this.renderBackground(pose);

        RenderSystem.blendColor(1f, 1f, 1f, 1f);
        minecraft.textureManager.bind(LAPTOP_GUI);

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
        IngameGui.blit(pose, posX + BORDER, posY, ClientLaptop.SCREEN_WIDTH, BORDER, 10, 0, 1, BORDER, 256, 256); // TOP
        IngameGui.blit(pose, posX + ClientLaptop.DEVICE_WIDTH - BORDER, posY + BORDER, BORDER, ClientLaptop.SCREEN_HEIGHT, 11, 10, BORDER, 1, 256, 256); // RIGHT
        IngameGui.blit(pose, posX + BORDER, posY + ClientLaptop.DEVICE_HEIGHT - BORDER, ClientLaptop.SCREEN_WIDTH, BORDER, 10, 11, 1, BORDER, 256, 256); // BOTTOM
        IngameGui.blit(pose, posX, posY + BORDER, BORDER, ClientLaptop.SCREEN_HEIGHT, 0, 11, BORDER, 1, 256, 256); // LEFT

        // Center
        IngameGui.blit(pose, posX + BORDER, posY + BORDER, ClientLaptop.SCREEN_WIDTH, ClientLaptop.SCREEN_HEIGHT, 10, 10, 1, 1, 256, 256);

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTick) {
        int posX = (width - ClientLaptop.DEVICE_WIDTH) / 2 + BORDER;
        int posY = (height - ClientLaptop.DEVICE_HEIGHT) / 2 + BORDER;
        super.render(matrices, mouseX, mouseY, partialTick);
        renderBezels(matrices, mouseX, mouseY, partialTick);
        matrices.translate(posX, posY, 0);
        laptop.render(matrices, mouseX-posX, mouseY-posY, partialTick);
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
