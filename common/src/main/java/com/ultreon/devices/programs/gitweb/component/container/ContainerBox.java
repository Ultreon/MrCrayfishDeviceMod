package com.ultreon.devices.programs.gitweb.component.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.ultreon.devices.Reference;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author MrCrayfish
 */
public abstract class ContainerBox extends Component {
    public static final int WIDTH = 128;
    protected static final ResourceLocation CONTAINER_BOXES_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/container_boxes.png");
    protected List<Slot> slots = new ArrayList<>();
    protected int boxU, boxV;
    protected int height;
    protected ItemStack icon;
    protected String title;

    public ContainerBox(int left, int top, int boxU, int boxV, int height, ItemStack icon, String title) {
        super(left, top);
        this.boxU = boxU;
        this.boxV = boxV;
        this.height = height;
        this.icon = icon;
        this.title = title;
    }

    @Override
    protected void render(MatrixStack pose, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        mc.textureManager.bind(CONTAINER_BOXES_TEXTURE);
        RenderUtil.drawRectWithTexture(pose, x, y + 12, boxU, boxV, WIDTH, height, WIDTH, height, 256, 256);
        //Gui.blit(pose, x, y + 12, WIDTH, height, boxU, boxV, 256, 256, WIDTH, height);

        int contentOffset = (WIDTH - (Laptop.getFont().width(title) + 8 + 4)) / 2;
        pose.pushPose();
        {
            pose.translate(x + contentOffset, y, 0);
            pose.scale(0.5f, 0.5f, 0.5f);
            RenderUtil.renderItem(x+contentOffset-5, y-4, icon, false);
        }
        pose.popPose();

        RenderUtil.drawStringClipped(pose, title, x + contentOffset + 8 + 4, y, 110, Color.WHITE.getRGB(), true);

        slots.forEach(slot -> slot.render(x, y + 12));
    }

    @Override
    protected void renderOverlay(MatrixStack pose, Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
        slots.forEach(slot -> slot.renderOverlay(pose, laptop, xPosition, yPosition + 12, mouseX, mouseY));
    }

    protected static class Slot {
        private final int slotX;
        private final int slotY;
        private final ItemStack stack;

        public Slot(int slotX, int slotY, ItemStack stack) {
            this.slotX = slotX;
            this.slotY = slotY;
            this.stack = stack;
        }

        public void render(int x, int y) {
            RenderUtil.renderItem(x + slotX, y + slotY, stack, true);
        }

        public void renderOverlay(MatrixStack pose, Laptop laptop, int x, int y, int mouseX, int mouseY) {
            if (GuiHelper.isMouseWithin(mouseX, mouseY, x + slotX, y + slotY, 16, 16)) {
                if (!stack.isEmpty()) {
                    laptop.renderTooltip(pose, laptop.getTooltipFromItem(stack), Optional.empty(), mouseX, mouseY/*, stack*/);
                }
            }

            // Todo: fix this.
//            GlStateManager.disableRescaleNormal();
//            RenderHelper.disableStandardItemLighting();
//            GlStateManager.disableDepth();
        }

        public ItemStack getStack() {
            return stack;
        }
    }
}
