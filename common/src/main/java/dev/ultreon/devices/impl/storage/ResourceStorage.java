package dev.ultreon.devices.impl.storage;

import dev.ultreon.devices.api.storage.DataStorage;
import dev.ultreon.devices.api.storage.FileHandle;
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
