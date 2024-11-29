package com.ultreon.devices.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Supplier;

public abstract class Packet<T extends Packet<T>> implements CustomPacketPayload {
    public Packet() {

    }

    public abstract void toBytes(RegistryFriendlyByteBuf buf);

    @Deprecated
    public void fromBytes(RegistryFriendlyByteBuf buf) {

    }

    public abstract boolean onMessage(Supplier<NetworkManager.PacketContext> ctx);
}
