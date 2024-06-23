package com.ultreon.devices.core.laptop.server;

import com.ultreon.devices.core.laptop.common.S2CUpdatePacket;
import com.ultreon.devices.network.PacketHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class ServerLaptop {
    public static HashMap<UUID, ServerLaptop> laptops = new HashMap<>();
    private final UUID uuid = new UUID(430985038594038L, 493058808830598L);
    public void sendPacket(PlayerEntity player, String type, CompoundNBT nbt) {
        PacketHandler.sendToClient(new S2CUpdatePacket(this.uuid, type, nbt), player);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void handlePacket(PlayerEntity player, String type, CompoundNBT data) {
        System.out.printf("Handling %s, %s%n", type, data);
        if (type.equals("mouseMoved")) {
            var x = data.getDouble("x");
            var y = data.getDouble("y");
            sendPacket(player, "placeSquare", data);
        }
    }
}
