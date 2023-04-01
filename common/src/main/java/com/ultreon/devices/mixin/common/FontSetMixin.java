package com.ultreon.devices.mixin.common;

import com.mojang.blaze3d.font.GlyphInfo;
import com.ultreon.devices.Devices;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontSet.class)
public class FontSetMixin {
//    @Shadow @Final private ResourceLocation name;
//    private static final GlyphInfo DEVICES_TAB_INFO = () -> 16.0f;
//
//    @Inject(method = "getGlyphInfoForSpace", at = @At("HEAD"))
//    public void getGlyphInfoForSpace(int i, CallbackInfoReturnable<GlyphInfo> cir) {
//        if (name.equals(Devices.res("laptop")) && i == 9) {
//            cir.setReturnValue(DEVICES_TAB_INFO);
//        }
//    }
}
