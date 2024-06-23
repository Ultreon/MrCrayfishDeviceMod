package com.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.init.DeviceItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;

public class LaptopRenderer extends TileEntityRenderer<LaptopBlockEntity> {
    private final Minecraft mc = Minecraft.getInstance();
    private final TileEntityRendererDispatcher dispatcher;

    public LaptopRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
        this.dispatcher = dispatcher;
    }

    @Override
    public void render(LaptopBlockEntity blockEntity, float partialTick, MatrixStack matrices, @NotNull IRenderTypeBuffer bufferSource, int packedLight, int packedOverlay) {
//        poseStack.pushPose();
//        RenderSystem.depthMask(true);
//        poseStack.scale(0.005f, 0.005f, -1.0f);
//        poseStack.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, 180, 180)));
//        var l = new ClientLaptop();
//        l.render(poseStack, -999, -999, partialTick);
//        RenderSystem.depthMask(true);
//        poseStack.popPose();
        float direction = blockEntity.getBlockState().getValue(LaptopBlock.FACING).getClockWise().toYRot();
        ItemEntity entityItem = new ItemEntity(Minecraft.getInstance().level, 0D, 0D, 0D, ItemStack.EMPTY);
        BlockState state = blockEntity.getBlock().defaultBlockState().setValue(LaptopBlock.TYPE, LaptopBlock.Type.SCREEN);
        BlockPos pos = blockEntity.getBlockPos();

        mc.textureManager.bind(PlayerContainer.BLOCK_ATLAS);
        matrices.pushPose();

        int x = blockEntity.getBlockPos().getX();
        int y = blockEntity.getBlockPos().getY();
        int z = blockEntity.getBlockPos().getZ();
        //poseStack.pushPose();
        {
            //poseStack.translate(x, y, z);

            if (blockEntity.isExternalDriveAttached()) {
                matrices.pushPose();
                {
                    matrices.translate(0.5, 0, 0.5);
                    matrices.mulPose(blockEntity.getBlockState().getValue(LaptopBlock.FACING).getRotation());
                    matrices.translate(-0.5, 0, -0.5);
                    matrices.translate(0.595, -0.2075, -0.005);
                    assert DeviceItems.getFlashDriveByColor(blockEntity.getExternalDriveColor()) != null;
                    entityItem.setItem(new ItemStack(DeviceItems.getFlashDriveByColor(blockEntity.getExternalDriveColor()), 1/*, blockEntity.getExternalDriveColor().*/));
                    renderEntity(entityItem, 0.0D, 0.0D, 0.0D, 0.0F, matrices, bufferSource/*, 0.0F, false*/);
                    matrices.translate(0.1, 0, 0);
                }
                matrices.popPose();
            }

            matrices.pushPose();
            {
                //System.out.println("RENDEEING");
                matrices.translate(0.5, 0, 0.5);//west/east +90 north/south -90
                matrices.mulPose(Vector3f.YP.rotationDegrees(blockEntity.getBlockState().getValue(LaptopBlock.FACING) == Direction.EAST || blockEntity.getBlockState().getValue(LaptopBlock.FACING) == Direction.WEST ? direction + 90 : direction - 90));
                matrices.translate(-0.5, 0, -0.5);
                matrices.translate(0, 0.0625, 0.25);
                matrices.mulPose(Vector3f.XP.rotationDegrees(blockEntity.getScreenAngle(partialTick) + 180));
                //poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
                matrices.mulPose(Vector3f.XP.rotationDegrees(180));
                RenderHelper.setupForFlatItems();
                //      Tesselator tessellator = Tesselator.getInstance();
                //BufferBuilder buffer = tessellator.getBuilder();
                //buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
                //    poseStack.pushPose();
                //poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());

                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
                IBakedModel ibakedmodel = mc.getBlockRenderer().getBlockModel(state);
                matrices.pushPose();
                //poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
                //poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
                //poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
                blockrendererdispatcher.renderSingleBlock(state, matrices, bufferSource, packedLight, packedOverlay);//.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.cutout()), state, ibakedmodel, 1, 1, 1, packedLight, packedOverlay);
                matrices.popPose();
                //poseStack.popPose();
                //    tessellator.end();
                RenderHelper.setupFor3DItems();
            }
            matrices.popPose();
        }
        matrices.popPose();
    }

    private void renderEntity(Entity p_228418_1_, double p_228418_2_, double p_228418_4_, double p_228418_6_, float p_228418_8_, MatrixStack p_228418_9_, IRenderTypeBuffer p_228418_10_) {
        double d0 = MathHelper.lerp(p_228418_8_, p_228418_1_.xOld, p_228418_1_.getX());
        double d1 = MathHelper.lerp(p_228418_8_, p_228418_1_.yOld, p_228418_1_.getY());
        double d2 = MathHelper.lerp(p_228418_8_, p_228418_1_.zOld, p_228418_1_.getZ());
        float f = MathHelper.lerp(p_228418_8_, p_228418_1_.yRotO, p_228418_1_.yRot);
        mc.getEntityRenderDispatcher().render(p_228418_1_, d0 - p_228418_2_, d1 - p_228418_4_, d2 - p_228418_6_, f, p_228418_8_, p_228418_9_, p_228418_10_, mc.getEntityRenderDispatcher().getPackedLightCoords(p_228418_1_, p_228418_8_));
    }

}