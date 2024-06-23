package com.ultreon.devices.network.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.network.Packet;
import com.ultreon.devices.network.PacketHandler;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.Objects;
import java.util.function.Supplier;

public class RequestPacket extends Packet<RequestPacket> {
    private final int id;
    private final Task request;
    private CompoundNBT tag;

    public RequestPacket(PacketBuffer buf) {
        this.id = buf.readInt();
        String name = buf.readUtf();
        this.request = TaskManager.getTask(name);
        this.tag = buf.readNbt();
        //System.out.println("decoding");
    }

    public RequestPacket(int id, Task request) {
        this.id = id;
        this.request = request;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.id);
        buf.writeUtf(this.request.getName());
        CompoundNBT tag = new CompoundNBT();
        this.request.prepareRequest(tag);
        buf.writeNbt(tag);
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        //System.out.println("RECEIVED from " + ctx.get().getPlayer().getUUID());
        request.processRequest(tag, Objects.requireNonNull(ctx.get().getSender()).level, ctx.get().getSender());
        if (ctx.get().getSender() instanceof ServerPlayerEntity player)
        PacketHandler.sendToClient(new ResponsePacket(id, request), player);
        return true;
    }

    public int getId() {
        return id;
    }

}
