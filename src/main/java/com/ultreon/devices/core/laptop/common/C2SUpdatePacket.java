package com.ultreon.devices.core.laptop.common;

import com.ultreon.devices.core.laptop.server.ServerLaptop;
import com.ultreon.devices.network.Packet;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class C2SUpdatePacket extends Packet<C2SUpdatePacket> {
    private final CompoundNBT nbt;

    public C2SUpdatePacket(UUID laptop, String type, CompoundNBT nbt) {
        this.nbt = new CompoundNBT();
        this.nbt.putUUID("uuid", laptop); // laptop uuid
        this.nbt.putString("type", type);
        this.nbt.put("data", nbt);
    }

    @Deprecated // do not call
    public C2SUpdatePacket(PacketBuffer buf) {
        this.nbt = buf.readNbt();
    }
    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().equals(LogicalSide.SERVER)) {
            ServerLaptop.laptops.get(this.nbt.getUUID("uuid")).handlePacket(ctx.get().getSender(), this.nbt.getString("type"), this.nbt.getCompound("data"));
        }
        return false;
    }
}
