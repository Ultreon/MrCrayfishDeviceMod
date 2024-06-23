package com.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import com.ultreon.devices.block.OfficeChairBlock;
import com.ultreon.devices.block.entity.OfficeChairBlockEntity;
import com.ultreon.devices.init.DeviceBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.block.BlockState;

public class OfficeChairRenderer extends TileEntityRenderer<OfficeChairBlockEntity> {
    private Minecraft mc = Minecraft.getInstance();

    public OfficeChairRenderer() {

    }

    @Override
    public void render(OfficeChairBlockEntity blockEntity, float partialTick, MatrixStack matrices, IRenderTypeBuffer bufferSource, int packedLight, int packedOverlay)
    {
        BlockPos pos = blockEntity.getBlockPos();
        BlockState tempState = blockEntity.getLevel().getBlockState(pos);
        if(!(tempState.getBlock() instanceof OfficeChairBlock))
        {
            return;
        }

        var x = pos.getX();
        var y = pos.getY();
        var z = pos.getZ();

        matrices.pushPose();
        {
           // poseStack.translate(x, y, z);

            matrices.translate(0.5, 0, 0.5);
            matrices.mulPose(Quaternion.fromXYZDegrees(new Vector3f(0, -blockEntity.getRotation()+180, 0)));
            matrices.translate(-0.5, 0, -0.5);

            BlockState state = tempState.setValue(OfficeChairBlock.FACING, Direction.NORTH).setValue(OfficeChairBlock.TYPE, OfficeChairBlock.Type.SEAT);

            RenderHelper.setupForFlatItems();
            //GlStateManager.enableTexture2D();

            mc.textureManager.bind(PlayerContainer.BLOCK_ATLAS);

            //Tessellator tessellator = Tessellator.getInstance();

            //BufferBuilder buffer = tessellator.getBuffer();
            //buffer.begin(7, DefaultVertexFormats.BLOCK);
            //buffer.setTranslation(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());

            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRenderer();
            blockrendererdispatcher.renderSingleBlock(state, matrices, bufferSource, packedLight, packedOverlay);


            RenderHelper.setupFor3DItems();
        }
        matrices.popPose();
    }
}