package com.ultreon.devices.core.laptop.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.*;
import net.minecraft.util.math.vector.Matrix4f;
import com.ultreon.devices.Devices;
import com.ultreon.devices.Reference;
import com.ultreon.devices.core.laptop.common.C2SUpdatePacket;
import com.ultreon.devices.core.laptop.common.TaskBar;
import com.ultreon.devices.core.laptop.server.ServerLaptop;
import com.ultreon.devices.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.Component;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.Inventory;
import net.minecraft.inventory.container.AbstractContainerMenu;
import net.minecraft.inventory.container.ChestMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static com.ultreon.devices.core.laptop.client.ClientLaptopScreen.LAPTOP_GUI;
import static net.minecraft.client.gui.AbstractGui.blit;


// NO STATICS
public class ClientLaptop {
    public static final HashMap<UUID, ClientLaptop> laptops = new HashMap<>(); // current active client laptops
    public static final int DEVICE_HEIGHT = 216;
    public static final int SCREEN_HEIGHT = DEVICE_HEIGHT - 20;
    public static final int DEVICE_WIDTH = 384;
    public static final int SCREEN_WIDTH = DEVICE_WIDTH - 20;

    private UUID uuid;
    public final double[] square = new double[2];

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    private TaskBar taskbar = new TaskBar(this);
    public ClientLaptop() {
        //super(Component.translatable("laptop")); //todo
    }

    public void handlePacket(String type, CompoundNBT nbt) {
        if (type.equals("placeSquare")) {
            System.out.println("moving square lol");
            System.out.println(nbt);
            square[0] = nbt.getDouble("x");
            square[1] = nbt.getDouble("y");
            System.out.println("SET");
        }
    }

    public void sendPacket(String type, CompoundNBT nbt) {
        System.out.printf("Sending packet %s, %s%n", type, nbt);
        PacketHandler.sendToServer(new C2SUpdatePacket(this.uuid, type, nbt));
    }

    public void renderBezels(final @NotNull MatrixStack pose, final int mouseX, final int mouseY, float partialTicks) { // no bezels

    }

    //@Override
    public void render(final @NotNull MatrixStack pose, final int mouseX, final int mouseY, float partialTicks) {
        double[] square = new double[2];
        Minecraft.getInstance().doRunTask(() -> {
            square[0] = this.square[0];
            square[1] = this.square[1];
        });
        RenderSystem.blendColor(1f, 1f, 1f, 1f);
        mc.textureManager.bind(LAPTOP_GUI);
        //RenderSystem.disableBlend();
        IngameGui.blit(pose, 0, 0, ClientLaptop.SCREEN_WIDTH, ClientLaptop.SCREEN_HEIGHT, 10, 10, 1, 1, 256, 256);
        //RenderSystem.enableBlend();

        Minecraft.getInstance().font.draw(pose, "New Laptop System 0.01% complete", 0, 0, 0xffffff);
        IngameGui.fill(pose, 0, 0, 10, 10, 0x2e2e2e);
        taskbar.render(pose, this, Minecraft.getInstance(), 0, SCREEN_HEIGHT-16, mouseX, mouseY, partialTicks);
        System.out.println("x = " + square[0]);
        IngameGui.fill(pose, (int) square[0], (int) square[1], (int) square[0]+10, (int) square[1]+10, 0xffffff);
    }

    public void mouseMoved(double mouseX, double mouseY) {
        var nbt = new CompoundNBT();
        nbt.putDouble("x", mouseX);
        nbt.putDouble("y", mouseY);
        sendPacket("mouseMoved", nbt);
    }

    public UUID getUuid() {
        return uuid;
    }
}
