package com.ultreon.devices.block.entity.renderer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.ultreon.devices.Devices;
import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.debug.DebugLog;
import com.ultreon.devices.init.DeviceItems;
import com.ultreon.devices.item.FlashDriveItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LaptopRenderer implements BlockEntityRenderer<LaptopBlockEntity> {
    private final BlockEntityRendererProvider.Context context;
    private final Minecraft mc = Minecraft.getInstance();
    private final Cache<DyeColor, ItemStack> cache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.SECONDS).build();

    public LaptopRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(LaptopBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        var direction = blockEntity.getBlockState().getValue(LaptopBlock.FACING).getClockWise().toYRot();

        BlockState state = blockEntity.getBlock().defaultBlockState().setValue(LaptopBlock.TYPE, LaptopBlock.Type.SCREEN);

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        poseStack.pushPose();

        if (blockEntity.isExternalDriveAttached()) {
            poseStack.pushPose();
            //region <Render Flash Drive>

            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(blockEntity.getBlockState().getValue(LaptopBlock.FACING).getRotation());
            poseStack.mulPose(Quaternion.fromXYZ(0, 0, (float) (-Math.PI / 2)));
            poseStack.mulPose(Quaternion.fromXYZ((float) (-Math.PI / 2), 0, 0));
            poseStack.translate(-0.5, 0, -0.5);
            poseStack.translate(0.595, -(3 / 64f), -0.005);

            DyeColor driveColor = blockEntity.getExternalDriveColor();
            FlashDriveItem flashDriveByColor = DeviceItems.getFlashDriveByColor(DyeColor.WHITE);
            if (flashDriveByColor == null)
                throw new IllegalStateException("Failed to get flash drive item by color.");
            ItemStack stack;
            try {
                stack = this.cache.get(driveColor, flashDriveByColor::getDefaultInstance);
            } catch (ExecutionException e) {
                Devices.LOGGER.error("Failed to get flash drive item stack from cache.", e);
                stack = flashDriveByColor.getDefaultInstance();
            }
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, packedLight, packedOverlay, poseStack, bufferSource, 0);

            //endregion
            poseStack.popPose();
        }

        poseStack.pushPose();
        //region <Render Laptop>

        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(blockEntity.getBlockState().getValue(LaptopBlock.FACING) == Direction.EAST || blockEntity.getBlockState().getValue(LaptopBlock.FACING) == Direction.WEST ? direction + 90 : direction - 90));
        poseStack.translate(-0.5, 0, -0.5);
        poseStack.translate(0, 0.0625, 0.25);
        poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(blockEntity.getScreenAngle(partialTick) + 180, 0, 0)));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
        Lighting.setupForFlatItems();

        //region <Render Model>
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        poseStack.pushPose();
        dispatcher.renderSingleBlock(state, poseStack, bufferSource, packedLight, packedOverlay);//.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.cutout()), state, ibakedmodel, 1, 1, 1, packedLight, packedOverlay);
        poseStack.popPose();
        //endregion

        Lighting.setupFor3DItems();
        poseStack.popPose();

        //endregion
        poseStack.popPose();
    }
}