package com.ultreon.devices.core.client;

import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * @author Qboi123
 */
public class LaptopFontSet extends Font {
    private static final IGlyph TAB_INFO = () -> 16.0f;

    public LaptopFontSet(TextureManager pTextureManager, ResourceLocation pName) {
        super(pTextureManager, pName);
    }

    @Nullable
    @Override
    public IGlyph getGlyphInfo(int i) {
        return i == 9 ? TAB_INFO : super.getGlyphInfo(i);
    }
}
