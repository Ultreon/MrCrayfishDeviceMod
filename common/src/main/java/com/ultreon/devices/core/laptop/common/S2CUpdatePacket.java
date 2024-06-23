package com.ultreon.devices.core.laptop.common;

import com.ultreon.devices.core.laptop.client.ClientLaptop;
import com.ultreon.devices.core.laptop.server.ServerLaptop;
import com.ultreon.devices.network.Packet;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Supplier;

public class S2CUpdatePacket extends Packet<S2CUpdatePacket> {
    private final CompoundNBT nbt;

    public S2CUpdatePacket(UUID laptop, String type, CompoundNBT nbt) {
        this.nbt = new CompoundNBT();
        this.nbt.putUUID("uuid", laptop); // laptop uuid
        this.nbt.putString("type", type);
        this.nbt.put("data", nbt);
    }

    @Deprecated // do not call
    public S2CUpdatePacket(PacketBuffer buf) {
        this.nbt = buf.readNbt();
    }
    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeNbt(nbt);
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().equals(LogicalSide.CLIENT)) {
            ClientLaptop.laptops.get(this.nbt.getUUID("uuid")).handlePacket(this.nbt.getString("type"), this.nbt.getCompound("data"));
            System.out.println("SQUARE: " + Arrays.toString(ClientLaptop.laptops.get(this.nbt.getUUID("uuid")).square));
        }
        return false;
    }
}
