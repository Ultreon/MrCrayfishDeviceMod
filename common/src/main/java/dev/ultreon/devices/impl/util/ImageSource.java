package dev.ultreon.devices.impl.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface ImageSource {
    ResourceLocation resourcePath();

    default CompoundTag save(CompoundTag mapType) {
        mapType.putString("resourcePath", resourcePath().toString());
        return mapType;
    }
}
