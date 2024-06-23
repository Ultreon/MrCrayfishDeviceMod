package com.ultreon.devices.core.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.api.app.IIcon;
import com.ultreon.devices.api.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;

/**
 * @author MrCrayfish
 */
public class ClientNotification implements IToast {
    private static final ResourceLocation TEXTURE_TOASTS = new ResourceLocation("devices:textures/gui/toast.png");

    private IIcon icon;
    private String title;
    private String subTitle;

    private ClientNotification() {
    }

    @NotNull
    @Override
    public Visibility render(@NotNull MatrixStack pose, ToastGui toastComponent, long timeSinceLastVisible) {
        RenderSystem.blendColor(1f, 1f, 1f, 1f);
        Minecraft.getInstance().textureManager.bind(TEXTURE_TOASTS);
        toastComponent.blit(pose, 0, 0, 0, 0, 160, 32);
        FontRenderer font = toastComponent.getMinecraft().font;

        if (subTitle == null) {
            font.drawShadow(pose, font.plainSubstrByWidth(I18n.get(title), 118), 38, 12, -1);
        } else {
            font.drawShadow(pose, font.plainSubstrByWidth(I18n.get(title), 118), 38, 7, -1);
            font.draw(pose, font.plainSubstrByWidth(I18n.get(subTitle), 118), 38, 18, -1);
        }

        Minecraft.getInstance().textureManager.bind(icon.getIconAsset());
        RenderUtil.drawRectWithTexture(pose, 6, 6, icon.getGridWidth(), icon.getGridHeight(), icon.getU(), icon.getV(), icon.getSourceWidth(), icon.getSourceHeight(), icon.getIconSize(), icon.getIconSize());

        return timeSinceLastVisible >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }

    public static ClientNotification loadFromTag(CompoundNBT tag) {
        ClientNotification notification = new ClientNotification();

        int ordinal = tag.getCompound("icon").getInt("ordinal");
        String className = tag.getCompound("icon").getString("className");

        try {
            notification.icon = (IIcon) Class.forName(className).getEnumConstants()[ordinal];
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        notification.title = tag.getString("title");
        if (tag.contains("subTitle", Constants.NBT.TAG_STRING)) {
            notification.subTitle = tag.getString("subTitle");
        }

        return notification;
    }

    public void push() {
        Minecraft.getInstance().getToasts().addToast(this);
    }
}
