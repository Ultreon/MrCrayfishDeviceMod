package dev.ultreon.devices.impl;

import net.minecraft.nbt.CompoundTag;

public interface WorldSavedData {
    void save(CompoundTag tag);

    void load(CompoundTag tag);
}
