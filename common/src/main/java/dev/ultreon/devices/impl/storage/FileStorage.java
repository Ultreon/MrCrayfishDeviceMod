package dev.ultreon.devices.impl.storage;

import java.util.UUID;

public class FileStorage implements DataStorage {
    private final UUID player;

    public FileStorage(UUID player) {
        this.player = player;
    }

    @Override
    public FileHandle get(String path) {
        return new FileStorageHandle(path, player);
    }

    @Override
    public String id() {
        return "resource-path";
    }
}
