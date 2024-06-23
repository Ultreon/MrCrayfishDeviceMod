package com.ultreon.devices.mixin.common;

import com.mojang.blaze3d.font.IGlyph;
import com.ultreon.devices.Devices;
import net.minecraft.client.gui.font.Font;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Font.class)
public class FontMixin {
    @Shadow @Final private ResourceLocation name;
    private static final IGlyph DEVICES_TAB_INFO = () -> 16.0f;

    @Inject(method = "getIGlyphForSpace", at = @At("HEAD"))
    public void getIGlyphForSpace(int i, CallbackInfoReturnable<IGlyph> cir) {
        if (name.equals(Devices.res("laptop")) && i == 9) {
            cir.setReturnValue(DEVICES_TAB_INFO);
        }
    }
}
