package com.ultreon.devices.core.client;

import com.mojang.blaze3d.font.IGlyph;
import net.minecraft.client.gui.font.Font;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * @author Qboi123
 */
public class LaptopFont extends Font {
    private static final IGlyph TAB_INFO = () -> 16.0f;

    public LaptopFont(TextureManager pTextureManager, ResourceLocation pName) {
        super(pTextureManager, pName);
    }

    @Nullable
    @Override
    public IGlyph getIGlyphForSpace(int i) {
        return i == 9 ? TAB_INFO : super.getIGlyphForSpace(i);
    }
}
