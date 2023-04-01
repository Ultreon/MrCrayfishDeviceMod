package com.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.laptop.client.ClientLaptop;
import com.ultreon.devices.init.DeviceItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class LaptopRenderer implements BlockEntityRenderer<LaptopBlockEntity> {
    private final BlockEntityRendererProvider.Context context;
    private final Minecraft mc = Minecraft.getInstance();

    public LaptopRenderer(BlockEntityRendererProvider.Context context) {
        this.context = context;
    }

    @Override
    public void render(LaptopBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
//        poseStack.pushPose();
//        RenderSystem.depthMask(true);
//        poseStack.scale(0.005f, 0.005f, -1.0f);
//        poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, 180, 180)));
//        var l = new ClientLaptop();
//        l.render(poseStack, -999, -999, partialTick);
//        RenderSystem.depthMask(true);
//        poseStack.popPose();
        var direction = blockEntity.getBlockState().getValue(LaptopBlock.FACING).getClockWise().toYRot();
        ItemEntity entityItem = new ItemEntity(Minecraft.getInstance().level, 0D, 0D, 0D, ItemStack.EMPTY);
        BlockState state = blockEntity.getBlock().defaultBlockState().setValue(LaptopBlock.TYPE, LaptopBlock.Type.SCREEN);
        BlockPos pos = blockEntity.getBlockPos();

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        poseStack.pushPose();

        int x = blockEntity.getBlockPos().getX();
        int y = blockEntity.getBlockPos().getY();
        int z = blockEntity.getBlockPos().getZ();
        //poseStack.pushPose();
        {
            //poseStack.translate(x, y, z);

            if (blockEntity.isExternalDriveAttached()) {
                poseStack.pushPose();
                {
                    poseStack.translate(0.5, 0, 0.5);
                    poseStack.mulPose(blockEntity.getBlockState().getValue(LaptopBlock.FACING).getRotation());
                    poseStack.translate(-0.5, 0, -0.5);
                    poseStack.translate(0.595, -0.2075, -0.005);
                    entityItem.flyDist = 0.0F;
                    assert DeviceItems.getFlashDriveByColor(blockEntity.getExternalDriveColor()) != null;
                    entityItem.setItem(new ItemStack(DeviceItems.getFlashDriveByColor(blockEntity.getExternalDriveColor()), 1/*, blockEntity.getExternalDriveColor().*/));
                    Minecraft.getInstance().levelRenderer.renderEntity(entityItem, 0.0D, 0.0D, 0.0D, 0.0F, poseStack, bufferSource/*, 0.0F, false*/);
                    poseStack.translate(0.1, 0, 0);
                }
                poseStack.popPose();
            }

            poseStack.pushPose();
            {
                //System.out.println("RENDEEING");
                poseStack.translate(0.5, 0, 0.5);//west/east +90 north/south -90
                poseStack.mulPose(Axis.YP.rotationDegrees(blockEntity.getBlockState().getValue(LaptopBlock.FACING) == Direction.EAST || blockEntity.getBlockState().getValue(LaptopBlock.FACING) == Direction.WEST ? direction + 90 : direction - 90));
                poseStack.translate(-0.5, 0, -0.5);
                poseStack.translate(0, 0.0625, 0.25);
                poseStack.mulPose(Axis.XP.rotationDegrees(blockEntity.getScreenAngle(partialTick) + 180));
                //poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
               // Lighting.setupForFlatItems();
                //      Tesselator tessellator = Tesselator.getInstance();
                //BufferBuilder buffer = tessellator.getBuilder();
                //buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
                //    poseStack.pushPose();
                //poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());

                BlockRenderDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
                BakedModel ibakedmodel = mc.getBlockRenderer().getBlockModel(state);
                poseStack.pushPose();
                //poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
                //poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
                //poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
                blockrendererdispatcher.renderSingleBlock(state, poseStack, bufferSource, packedLight, packedOverlay);//.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.cutout()), state, ibakedmodel, 1, 1, 1, packedLight, packedOverlay);
                poseStack.popPose();
                //poseStack.popPose();
                //    tessellator.end();
             //   Lighting.setupFor3DItems();
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}