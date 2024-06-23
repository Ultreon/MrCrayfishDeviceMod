package com.ultreon.devices.network;

import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraft.network.PacketBuffer;

import java.util.function.Supplier;

public abstract class Packet<T extends Packet<T>> {
    public Packet() {

    }

    public abstract void toBytes(PacketBuffer buf);

    @Deprecated
    public void fromBytes(PacketBuffer buf) {

    }

    public abstract boolean onMessage(Supplier<NetworkEvent.Context> ctx);
}
