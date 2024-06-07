package dev.ultreon.devices.impl.storage;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class Storages {
    public static final ResourceStorage RESOURCE = new ResourceStorage();

    public static FileStorage file(ServerPlayer player) {
        return new FileStorage(player.getUUID());
    }

    static FileStorage file(UUID player) {
        return new FileStorage(player);
    }
}
