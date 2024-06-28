package com.ultreon.devices.programs.gitweb.component.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import com.ultreon.devices.core.Laptop;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;

import java.util.ArrayList;
import java.util.List;

public class LoomBox extends ContainerBox {
    public static final int HEIGHT = 84;
    private final ItemStack banner;
    private final ItemStack dye;
    private final ItemStack pattern;
    private final ItemStack result;
    private final List<Pair<BannerPattern, DyeColor>> resultBannerPatterns;
    private final ModelRenderer flag;

    public LoomBox(ItemStack banner, ItemStack dye, ItemStack pattern, ItemStack result) {
        super(0, 0, 128, 72, HEIGHT, new ItemStack(Blocks.LOOM), "Loom");
        this.banner = banner;
        this.dye = dye;
        this.pattern = pattern;
        this.result = result;
        slots.add(new Slot(13, 26, this.banner));
        slots.add(new Slot(33, 26, this.dye));
        slots.add(new Slot(23, 45, this.pattern));
        slots.add(new Slot(94, 58, this.result));

        if (!result.isEmpty())
        this.resultBannerPatterns = BannerTileEntity.createPatterns(((BannerItem)this.result.getItem()).getColor(), BannerTileEntity.getItemPatterns(this.result));
        else
        this.resultBannerPatterns = new ArrayList<>();
        this.flag = BannerTileEntityRenderer.makeFlag();
    }

    @Override
    protected void render(MatrixStack pose, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        super.render(pose, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
        int i = x;//this.leftPos;
        int j = y+12;//this.topPos;
        if (result.isEmpty())return;
        RenderHelper.setupForFlatItems();
        IRenderTypeBuffer.Impl bufferSource = mc.renderBuffers().bufferSource();
        pose.pushPose();
        //pose.translate((double)(i + 139), (double)(j + 52), 0.0D);
        pose.translate(i+90d,j+52d,0.0D);
        pose.scale(24.0F, -24.0F, 1.0F);
        pose.translate(0.5D, 0.5D, 0.5D);
        float f = 0.6666667F;
        pose.scale(0.6666667F, -0.6666667F, -0.6666667F);
        this.flag.xRot = 0.0F;
        this.flag.y = -32.0F;
        BannerTileEntityRenderer.renderPatterns(pose, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, this.resultBannerPatterns);
        pose.popPose();
        bufferSource.endBatch();


    }
}
//128x84