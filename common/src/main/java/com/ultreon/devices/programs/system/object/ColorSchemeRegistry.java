package com.ultreon.devices.programs.system.object;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;

public class ColorSchemeRegistry {
    private static final BiMap<ResourceLocation, ColorScheme> COLOR_SCHEMES = HashBiMap.create();

    private ColorSchemeRegistry() {

    }

    public static ColorScheme getColorScheme(ResourceLocation id) {
        return COLOR_SCHEMES.get(id);
    }

    public static void register(ResourceLocation id, ColorScheme colorScheme) {
        COLOR_SCHEMES.put(id, colorScheme);
    }

    public static ResourceLocation getKey(ColorScheme colorScheme) {
        return COLOR_SCHEMES.inverse().get(colorScheme);
    }

    public static Iterable<ResourceLocation> getKeys() {
        return COLOR_SCHEMES.keySet();
    }

    public static Iterable<ColorScheme> getValues() {
        return COLOR_SCHEMES.values();
    }
}
