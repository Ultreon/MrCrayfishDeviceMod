package com.ultreon.devices.mixin.common;

import com.ultreon.devices.event.FeatureFlagRegisterEvent;
import net.minecraft.world.flag.FeatureFlagRegistry;
import net.minecraft.world.flag.FeatureFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FeatureFlags.class)
public class FeatureFlagsMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/flag/FeatureFlagRegistry$Builder;build()Lnet/minecraft/world/flag/FeatureFlagRegistry;"), method = "<clinit>", locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void motobox$injectExtraFeatures(CallbackInfo ci, FeatureFlagRegistry.Builder builder) {
        FeatureFlagRegisterEvent.EVENT.invoker().onRegisterFeatureFlags(builder);
    }
}