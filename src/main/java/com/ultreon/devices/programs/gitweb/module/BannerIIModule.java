package com.ultreon.devices.programs.gitweb.module;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.programs.gitweb.component.GitWebFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.BannerRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerBlockEntity;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ultreon.devices.programs.gitweb.module.ContainerModule.getItem;

public class BannerIIModule extends Module {
    @Override
    public String[] getRequiredData() {
        return new String[]{"banner"};
    }

    @Override
    public String[] getOptionalData() {
        return new String[]{"waving"};
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width) {
        return LoomBox.HEIGHT;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
        layout.addComponent(createContainer(data));
    }

    public LoomBox createContainer(Map<String, String> data) {
        return new LoomBox(getItem(data, "banner"), Boolean.parseBoolean(data.get("waving")));
    }

    public static class LoomBox extends Component {
        public static final int HEIGHT = 84;
        private final ItemStack banner;
        private final ModelRenderer flag;
        private final List<Pair<BannerPattern, DyeColor>> resultBannerPatterns;

        public LoomBox(ItemStack banner, boolean waving) {
            super(0, 0);
            this.banner = banner;
            this.flag = Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BANNER).getChild("flag");

            if (!banner.isEmpty())
                this.resultBannerPatterns = BannerBlockEntity.createPatterns(((BannerItem)this.banner.getItem()).getColor(), BannerBlockEntity.getItemPatterns(this.banner));
            else
                this.resultBannerPatterns = new ArrayList<>();
        }

        @Override
        protected void render(MatrixStack pose, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
            super.render(pose, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
            int i = x;//this.leftPos;
            int j = y;//this.topPos;
            if (banner.isEmpty())return;
            RenderHelper.setupForFlatItems();
            IRenderTypeBuffer.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            pose.pushPose();
            //pose.translate((double)(i + 139), (double)(j + 52), 0.0D);
            pose.translate(i+139,j+90,0.0D);
            pose.scale(48.0F, -48.0F, 48.0F);
        //    pose.scale(24.0F, -24.0F, 1.0F);
            pose.translate(0.5D, 0.5D, 0.5D);
            float f = 0.6666667F;
            pose.scale(f, -f, -f);
            long l = System.currentTimeMillis()/50;
            System.out.println(l);
            float h = ((float)Math.floorMod(l, 100L) + partialTicks) / 100.0f;

            this.flag.yRot = (float) Math.toRadians(30);
            this.flag.xRot = (-0.0125f + 0.01f * MathHelper.cos((float)Math.PI * 2 * h)) * (float)Math.PI;
           // this.flag.xRot = 0.0F;
            this.flag.y = -32.0F;
            BannerRenderer.renderPatterns(pose, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, this.resultBannerPatterns);
            pose.popPose();
            bufferSource.endBatch();


        }
    }
}
// 128, 72