package com.ultreon.devices.util;

import net.minecraft.nbt.CompoundNBT;

public interface DataHandler {

    /**
     * Allows you to load data from a tag.
     *
     * @param tag the compound tag where you saved data is
     */
    void load(CompoundNBT tag);

    /**
     * Allows you to save data to a tag.
     *
     * @param tag the compound tag to save your data to
     */
    void save(CompoundNBT tag);
}
