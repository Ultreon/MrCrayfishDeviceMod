package com.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.core.network.NetworkDevice;
import com.ultreon.devices.core.network.Router;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL20;

import java.util.Collection;
import java.util.Objects;

/**
 * @author MrCrayfish
 */
public final class RouterRenderer extends TileEntityRenderer<RouterBlockEntity> {
    public RouterRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(RouterBlockEntity blockEntity, float partialTick, @NotNull MatrixStack pose, @NotNull IRenderTypeBuffer bufferSource, int packedLight, int packedOverlay) {
        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
        if (state.getBlock() != blockEntity.getBlock()) return;

        if (blockEntity.isDebug()) {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
//            RenderSystem.disableLighting();
            RenderSystem.disableTexture();
//            RenderSystem.enableAlpha();
            pose.pushPose();
            {
                pose.translate(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());
                Router router = blockEntity.getRouter();
                BlockPos routerPos = router.getPos();

                Vector3f linePositions = getLineStartPosition(state);
                final double startLineX = linePositions.x();
                final double startLineY = linePositions.y();
                final double startLineZ = linePositions.z();

                Tessellator tesselator = Tessellator.getInstance();
                BufferBuilder buffer = tesselator.getBuilder();

                final Collection<NetworkDevice> DEVICES = router.getConnectedDevices(Minecraft.getInstance().level);
                DEVICES.forEach(networkDevice -> {
                    BlockPos devicePos = networkDevice.getPos();

                    Objects.requireNonNull(devicePos, "NetworkManager device has no position, weird.");

                    RenderSystem.lineWidth(14F);
                    buffer.begin(GL20.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                    buffer.vertex(startLineX, startLineY, startLineZ).color(0f, 0f, 0f, 0.5f).endVertex();
                    buffer.vertex((devicePos.getX() - routerPos.getX()) + 0.5f, (devicePos.getY() - routerPos.getY()), (devicePos.getZ() - routerPos.getZ()) + 0.5f).color(1f, 1f, 1f, 0.35f).endVertex();
                    tesselator.end();

                    RenderSystem.lineWidth(4F);
                    buffer.begin(GL20.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
                    buffer.vertex(startLineX, startLineY, startLineZ).color(0f, 0f, 0f, 0.5f).endVertex();
                    buffer.vertex((devicePos.getX() - routerPos.getX()) + 0.5f, (devicePos.getY() - routerPos.getY()), (devicePos.getZ() - routerPos.getZ()) + 0.5f).color(0f, 1f, 0f, 0.5f).endVertex();
                    tesselator.end();
                });
            }
            pose.popPose();
            RenderSystem.disableBlend();
//            RenderSystem.disableAlpha();
//            RenderSystem.enableLighting();
            RenderSystem.enableTexture();
        }
    }

    private Vector3f getLineStartPosition(BlockState state) {
        float lineX = 0.5f;
        float lineY = 0.1f;
        float lineZ = 0.5f;

//        if (state.getValue(RouterBlock.VERTICAL)) {
//            Quaternion rotation = state.getValue(PrinterBlock.FACING).getRotation();
//            rotation.mul(new Quaternion((float) (14 * 0.0625), 0.5f, (float) (14 * 0.0625), 0.5f));
//            Vector3f fixedPosition = rotation.toXYZ();
//            lineX = fixedPosition.x();
//            lineY = 0.35f;
//            lineZ = fixedPosition.z();
//        }

//        return new Vec3(lineX, lineY, lineZ);
        return new Vector3f();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass() == this.getClass();
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "RouterRenderer[]";
    }

}
