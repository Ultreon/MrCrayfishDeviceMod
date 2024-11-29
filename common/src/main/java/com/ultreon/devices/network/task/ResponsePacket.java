package com.ultreon.devices.network.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.network.Packet;
import com.ultreon.devices.network.PacketHandler;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ResponsePacket extends Packet<ResponsePacket> {
    private final int id;
    private final Task request;
    private CompoundTag tag;

    public ResponsePacket(RegistryFriendlyByteBuf buf) {
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
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeBoolean(this.request.isSucessful());
        buf.writeUtf(this.request.getName());
        CompoundTag tag = new CompoundTag();
        this.request.prepareResponse(tag);
        buf.writeNbt(tag);
        this.request.complete();
    }

    @Override
    public boolean onMessage(Supplier<NetworkManager.PacketContext> ctx) {
        request.processResponse(tag);
        request.callback(tag);
        return false;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PacketHandler.getResponsePacket();
    }
}
