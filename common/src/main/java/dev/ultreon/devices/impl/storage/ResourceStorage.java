package dev.ultreon.devices.impl.storage;

import net.minecraft.resources.ResourceLocation;

public class ResourceStorage implements DataStorage {
    @Override
    public FileHandle get(String path) {
        return new ResourceFileHandle(new ResourceLocation(path));
    }

    @Override
    public String id() {
        return "resource-path";
    }
}
