package com.ultreon.devices.quilt;

import com.ultreon.devices.DevicesModPlatform;
import org.quiltmc.loader.api.QuiltLoader;

public class DevicesModQuiltPlatform implements DevicesModPlatform {

    @Override
    public String getVersion() {
        return QuiltLoader.getModContainer("devices").orElseThrow().metadata().version().raw();
    }
}
