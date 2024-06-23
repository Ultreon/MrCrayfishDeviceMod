package com.ultreon.devices.core.client;

import com.mojang.blaze3d.font.GlyphInfo;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * @author Qboi123
 */
public class LaptopFontSet extends FontSet {
    private static final GlyphInfo TAB_INFO = () -> 16.0f;

    public LaptopFontSet(TextureManager pTextureManager, ResourceLocation pName) {
        super(pTextureManager, pName);
    }

    @Nullable
    @Override
    public GlyphInfo getGlyphInfoForSpace(int i) {
        return i == 9 ? TAB_INFO : super.getGlyphInfoForSpace(i);
    }
}
