package com.jab125.clientint.mixin;

import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LanguageManager.class)
public interface LanguageManagerAccessor {
    @Accessor
    static LanguageInfo getDEFAULT_LANGUAGE() {
        throw new UnsupportedOperationException();
    }
}
