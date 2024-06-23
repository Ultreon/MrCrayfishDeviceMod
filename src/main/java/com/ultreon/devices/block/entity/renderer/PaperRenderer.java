package com.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.print.PrintingManager;
import com.ultreon.devices.block.PaperBlock;
import com.ultreon.devices.block.entity.PaperBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

/**
 * @author MrCrayfish
 */
public final class PaperRenderer extends TileEntityRenderer<PaperBlockEntity> {

    private Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings("SameParameterValue")
    private static void drawCuboid(double x, double y, double z, double width, double height, double depth, IRenderTypeBuffer bufferSource) {
        x /= 16;
        y /= 16;
        z /= 16;
        width /= 16;
        height /= 16;
        depth /= 16;
//        RenderSystem.disableLighting();
//        GlStateManager.enableRescaleNormal();
//        pose.glNormal3f(0f, 1f, 0f);
        double v = x + width + 1 - (width + width);
        drawQuad(x + (1 - width), y, z, x + width + (1 - width), y + height, z, Direction.NORTH, bufferSource);
        drawQuad(x + 1, y, z, x + 1, y + height, z + depth, Direction.EAST, bufferSource);
        drawQuad(v, y, z + depth, v, y + height, z, Direction.WEST, bufferSource);
        drawQuad(x + (1 - width), y, z + depth, x + width + (1 - width), y, z, Direction.DOWN, bufferSource);
        drawQuad(x + (1 - width), y + height, z, x + width + (1 - width), y, z + depth, Direction.UP, bufferSource);
//        GlStateManager.disableRescaleNormal();
//        GlStateManager.enableLighting();
    }

    private static void drawQuad(double xFrom, double yFrom, double zFrom, double xTo, double yTo, double zTo, Direction direction, IRenderTypeBuffer bufferSource) {
        double textureWidth = Math.abs(xTo - xFrom);
        double textureHeight = Math.abs(yTo - yFrom);
        double textureDepth = Math.abs(zTo - zFrom);
        IVertexBuilder buffer = bufferSource.getBuffer(RenderType.solid());

    }

    private static long AA = 0;

    /**
     *
     */
    public PaperRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    private static void drawPixels(MatrixStack matrices, int[] pixels, int resolution, boolean cut, int packedLight, IRenderTypeBuffer bufferSource) {
        double scale = 16 / (double) resolution;
        DynamicTexture d = new DynamicTexture(resolution, resolution, true);
        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {

                int r = (pixels[j + i * resolution] >> 16 & 255);
                int g = (pixels[j + i * resolution] >> 8 & 255);
                int b = (pixels[j + i * resolution] & 255);
                int a = (int) Math.floor((pixels[j + i * resolution] >> 24 & 255));
                assert d.getPixels() != null;
                d.getPixels().setPixelRGBA(i, j, new Color(r, g, b, a).getRGB());
            }
        }
        ResourceLocation resourcelocation = Minecraft.getInstance().getTextureManager().register("map/" + AA, d);
        Matrix4f matrix4f = matrices.last().pose();
        var vertexconsumer = bufferSource.getBuffer(RenderType.text(resourcelocation));
        vertexconsumer.vertex(matrix4f, 0.0f, 128.0f, -0.01f).color(255, 255, 255, 255).uv(0.0f, 1.0f).uv2(packedLight).endVertex();
        vertexconsumer.vertex(matrix4f, 128.0f, 128.0f, -0.01f).color(255, 255, 255, 255).uv(1.0f, 1.0f).uv2(packedLight).endVertex();
        vertexconsumer.vertex(matrix4f, 128.0f, 0.0f, -0.01f).color(255, 255, 255, 255).uv(1.0f, 0.0f).uv2(packedLight).endVertex();
        vertexconsumer.vertex(matrix4f, 0.0f, 0.0f, -0.01f).color(255, 255, 255, 255).uv(0.0f, 0.0f).uv2(packedLight).endVertex();
        AA++;
    }

    @Override
    public void render(PaperBlockEntity blockEntity, float partialTick, @NotNull MatrixStack pose, @NotNull IRenderTypeBuffer bufferSource, int packedLight, int packedOverlay) {
        BlockState state = Objects.requireNonNull(blockEntity.getLevel()).getBlockState(blockEntity.getBlockPos());
        if (blockEntity.getBlockState().getBlock() != state.getBlock()) {
            Devices.LOGGER.error("Paper block mismatch: {} != {}", blockEntity.getBlockState().getBlock(), state.getBlock());
            return;
        }

        pose.pushPose();
        {
            pose.translate(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());
            pose.translate(0.5, 0.5, 0.5);
            pose.mulPose(state.getValue(PaperBlock.FACING).getRotation());
            pose.mulPose(new Quaternion(0, 0, 1, -blockEntity.getRotation()));
            pose.translate(-0.5, -0.5, -0.5);

            IPrint print = blockEntity.getPrint();
            if (print != null) {
                CompoundNBT data = print.toTag();
                if (data.contains("pixels", Constants.NBT.TAG_INT_ARRAY) && data.contains("resolution", Constants.NBT.TAG_INT)) {
                    mc.textureManager.bind(PrinterRenderer.PaperModel.TEXTURE);
                    if (DeviceConfig.RENDER_PRINTED_3D.get() && !data.getBoolean("cut")) {
                        // drawCuboid(0, 0, 0, 16, 16, 1, bufferSource);
                    }

                    pose.translate(0, 0, DeviceConfig.RENDER_PRINTED_3D.get() ? 0.0625 : 0.001);

                    pose.pushPose();
                    {
                        IPrint.Renderer renderer = PrintingManager.getRenderer(print);
                        renderer.render(pose, data);
                    }
                    pose.popPose();

                    pose.pushPose();
                    {
                        if (DeviceConfig.RENDER_PRINTED_3D.get() && data.getBoolean("cut")) {
                            CompoundNBT tag = print.toTag();
                            drawPixels(pose, tag.getIntArray("pixels"), tag.getInt("resolution"), tag.getBoolean("cut"), packedLight, bufferSource);
                        }
                    }
                    pose.popPose();
                }
            }
        }
        pose.popPose();
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
        return "PaperRenderer[]";
    }

}
