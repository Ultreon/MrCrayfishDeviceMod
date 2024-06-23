package com.ultreon.devices.network.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.network.Packet;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.function.Supplier;

public class ResponsePacket extends Packet<ResponsePacket> {
    private final int id;
    private final Task request;
    private CompoundNBT tag;

    public ResponsePacket(PacketBuffer buf) {
        this.id = buf.readInt();
        boolean successful = buf.readBoolean();
        this.request = TaskManager.getTaskAndRemove(this.id);
        if (successful) this.request.setSuccessful();
        String name = buf.readUtf();
        this.tag = buf.readNbt();
    }

    public ResponsePacket(int id, Task request) {
        this.id = id;
        this.request = request;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeInt(this.id);
        buf.writeBoolean(this.request.isSucessful());
        buf.writeUtf(this.request.getName());
        CompoundNBT tag = new CompoundNBT();
        this.request.prepareResponse(tag);
        buf.writeNbt(tag);
        this.request.complete();
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        request.processResponse(tag);
        request.callback(tag);
        return false;
    }
}
