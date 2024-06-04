package dev.ultreon.devices.block.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.ultreon.devices.Display;
import dev.ultreon.devices.block.ComputerBlock;
import dev.ultreon.devices.block.LaptopBlock;
import dev.ultreon.devices.block.entity.LaptopBlockEntity;
import dev.ultreon.devices.client.DisplayGui;
import dev.ultreon.devices.init.DeviceItems;
import dev.ultreon.devices.util.GLHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

        ItemEntity entityItem = new ItemEntity(Minecraft.getInstance().level, 0, 0, 0, ItemStack.EMPTY) {
            @Override
            public float getSpin(float partialTicks) {
                return ((float)this.getAge() + partialTicks) / 20.0f + 0;
            }
        };

        entityItem.bobOffs = 0;
        entityItem.setYRot(0);
        BlockState state = blockEntity.getBlock().defaultBlockState().setValue(ComputerBlock.TYPE, LaptopBlock.Type.SCREEN);

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        poseStack.pushPose();
        {
            if (blockEntity.isExternalDriveAttached()) {
                poseStack.pushPose();
                {
                    poseStack.translate(0.5, 0, 0.5);
                    poseStack.mulPose(blockEntity.getBlockState().getValue(LaptopBlock.FACING).getRotation());
                    poseStack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(-90)));
                    poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-90)));
                    poseStack.translate(-0.5, 0, -0.5);
                    poseStack.translate(0.595, -0.2075, -0.005);
//                    poseStack.translate(0.1, 0, 0);

                    entityItem.flyDist = 0.0F;
                    assert DeviceItems.getFlashDriveByColor(blockEntity.getExternalDriveColor()) != null;
                    entityItem.setItem(new ItemStack(DeviceItems.getFlashDriveByColor(blockEntity.getExternalDriveColor()), 1/*, blockEntity.getExternalDriveColor().*/));
                    Minecraft.getInstance().getEntityRenderDispatcher().render(entityItem, 0, 0, 0, 0, 0, poseStack, bufferSource, packedLight);
                }
                poseStack.popPose();
            }

            poseStack.pushPose();
            {
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

                BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                BakedModel ibakedmodel = mc.getBlockRenderer().getBlockModel(state);
                poseStack.pushPose();
                //poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
                //poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
                //poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
                dispatcher.renderSingleBlock(state, poseStack, bufferSource, packedLight, packedOverlay);//.renderModel(poseStack.last(), bufferSource.getBuffer(RenderType.cutout()), state, ibakedmodel, 1, 1, 1, packedLight, packedOverlay);

                if (blockEntity.isPoweredOn()) {
                    GuiGraphics guiGraphics = new GuiGraphics(Minecraft.getInstance(), new FakeBufferSource(bufferSource));
                    guiGraphics.pose().pushPose();

                    poseStack.scale(1 / 320f, 1 / 320f, 1 / 320f);
                    poseStack.translate(0.5f, 0.5f, 0.0f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                    poseStack.translate(0, 0, 0);

                    guiGraphics.pose().last().pose().set(poseStack.last().pose());
                    guiGraphics.pose().last().normal().set(poseStack.last().normal());

                    DisplayGui open = (DisplayGui) DisplayGui.openInWorld(blockEntity);

                    GLHelper.disableScissor();

                    open.init(Minecraft.getInstance(), 1, 1);
                    open.renderOS(guiGraphics, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTick);

                    GLHelper.enableScissor();

                    guiGraphics.pose().popPose();

                    DisplayGui.closeInWorld();
                }

                poseStack.popPose();
                //poseStack.popPose();
                //    tessellator.end();
                //   Lighting.setupFor3DItems();
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private static class FakeBufferSource extends MultiBufferSource.BufferSource {
        private final MultiBufferSource bufferSource;

        public FakeBufferSource(MultiBufferSource bufferSource) {
            super(null, Map.of());
            this.bufferSource = bufferSource;
        }

        public VertexConsumer getBuffer(RenderType renderType) {
            return bufferSource.getBuffer(renderType);
        }

        public void endLastBatch() {
//                            if (this.lastState.isPresent()) {
//                                RenderType renderType = (RenderType)this.lastState.get();
//                                if (!this.fixedBuffers.containsKey(renderType)) {
//                                    this.endBatch(renderType);
//                                }
//
//                                this.lastState = Optional.empty();
//                            }
        }

        public void endBatch() {
//                            this.lastState.ifPresent((renderTypex) -> {
//                                VertexConsumer vertexConsumer = this.getBuffer(renderTypex);
//                                if (vertexConsumer == this.builder) {
//                                    this.endBatch(renderTypex);
//                                }
//
//                            });
//                            Iterator var1 = this.fixedBuffers.keySet().iterator();
//
//                            while(var1.hasNext()) {
//                                RenderType renderType = (RenderType)var1.next();
//                                this.endBatch(renderType);
//                            }

        }

        public void endBatch(RenderType renderType) {
//            BufferBuilder bufferBuilder = this.getBuilderRaw(renderType);
//            boolean bl = Objects.equals(this.lastState, renderType.asOptional());
//            if (bl || bufferBuilder != this.builder) {
//                if (this.startedBuffers.remove(bufferBuilder)) {
//                    renderType.end(bufferBuilder, RenderSystem.getVertexSorting());
//                    if (bl) {
//                        this.lastState = Optional.empty();
//                    }
//
//                }
//            }
        }
    }
}