package com.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.ultreon.devices.block.OfficeChairBlock;
import com.ultreon.devices.block.entity.OfficeChairBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;

public class OfficeChairRenderer extends TileEntityRenderer<OfficeChairBlockEntity> {
    private Minecraft mc = Minecraft.getInstance();

    public OfficeChairRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);

    }

    @Override
    public void render(OfficeChairBlockEntity blockEntity, float partialTick, @NotNull MatrixStack matrices, @NotNull IRenderTypeBuffer bufferSource, int packedLight, int packedOverlay)
    {
        BlockPos pos = blockEntity.getBlockPos();
        BlockState tempState = blockEntity.getLevel().getBlockState(pos);
        if(!(tempState.getBlock() instanceof OfficeChairBlock))
        {
            return;
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        matrices.pushPose();
        {
           // poseStack.translate(x, y, z);

            matrices.translate(0.5, 0, 0.5);
            matrices.mulPose(Vector3f.YP.rotationDegrees(-blockEntity.getRotation()+180));
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