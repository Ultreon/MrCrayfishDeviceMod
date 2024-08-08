package dev.ultreon.devices.impl.bios;

import net.minecraft.resources.ResourceLocation;

public record ExecMeta(
    String name,
    String description,
    String launcher,
    ResourceLocation icon
) {
}
