package dev.ultreon.devices;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Contract;

public enum ModDeviceTypes implements IDeviceType {
    COMPUTER, PRINTER, FLASH_DRIVE, ROUTER, SEAT;

    public static final Codec<ModDeviceTypes> CODEC = Codec.STRING.xmap(
            val -> ModDeviceTypes.valueOf(val.toUpperCase()),
            val -> val.name().toLowerCase()
    );

    @Override
    @Contract(pure = true, value = "-> this")
    public ModDeviceTypes getDeviceType() {
        return this;
    }
}
