package com.ultreon.devices.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.core.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import org.jetbrains.annotations.NotNull;

public class GuiButtonClose extends Button {
    public GuiButtonClose(int x, int y) {
        super(x, y, 11, 11, new StringTextComponent(""), (button) -> {

        });
    }

    @Override
    public void renderButton(@NotNull MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            FontRenderer font = Minecraft.getInstance().font;
            mc.textureManager.bind(Window.WINDOW_GUI);
            RenderSystem.blendColor(1f, 1f, 1f, 1f);
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
            RenderSystem.blendFunc(770, 771);

            int state = this.isHovered ? 1 : 0;
            blit(pose, this.x, this.y, state * this.width + 15, 0, this.width, this.height);
        }
    }

    public boolean isHovered() {
        return isHovered;
    }
}
