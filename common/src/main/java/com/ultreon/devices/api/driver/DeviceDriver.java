package com.ultreon.devices.api.driver;

import com.ultreon.devices.Devices;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.Optional;

public abstract class DeviceDriver {
    private final DriverMetadata metadata;
    private int id;

    protected DeviceDriver(ResourceLocation id) throws IOException {
        this(loadMetadata(id));
    }

    private static DriverMetadata loadMetadata(ResourceLocation id) throws IOException {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Optional<Resource> resource = resourceManager.getResource(new ResourceLocation(Devices.MOD_ID, "drivers/" + id + "/" + id.getPath() + ".json"));
        if (resource.isPresent()) {
            return Devices.GSON.fromJson(resource.get().openAsReader(), DriverMetadata.class);
        }

        throw new IOException("Driver metadata not found: " + id);

    }

    protected DeviceDriver(DriverMetadata metadata) {
        this.metadata = metadata;
    }

    public DriverMetadata getMetadata() {
        return metadata;
    }

    long createId() {
        if (this.id != 0) {
            return this.id;
        }

        return this.id = System.identityHashCode(this);
    }
}
