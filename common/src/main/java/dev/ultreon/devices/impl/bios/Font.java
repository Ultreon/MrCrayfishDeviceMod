package dev.ultreon.devices.impl.bios;

import net.minecraft.resources.ResourceLocation;

public interface Font {
    int width(String text);

    String plainSubstrByWidth(String text, int width);

    Iterable<String> splitLines(String s);

    int lineHeight();

    ResourceLocation resourcePath();
}
