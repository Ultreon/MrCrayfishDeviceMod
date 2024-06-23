package com.ultreon.devices.api;

import net.minecraft.nbt.CompoundNBT;

public interface WorldSavedData {
    void save(CompoundNBT tag);

    void load(CompoundNBT tag);
}
