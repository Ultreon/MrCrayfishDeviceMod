package com.ultreon.devices.network.task;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.network.Packet;
import com.ultreon.devices.network.PacketHandler;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/// @author MrCrayfish
public class SyncConfigPacket extends Packet<SyncConfigPacket> {
    public SyncConfigPacket() {

    }

    public SyncConfigPacket(RegistryFriendlyByteBuf buf) {
        DeviceConfig.readSyncTag(Objects.requireNonNull(buf.readNbt()));
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(DeviceConfig.writeSyncTag());
    }

    @Override
    public boolean onMessage(Supplier<NetworkManager.PacketContext> ctx) {
        return true;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PacketHandler.getSyncConfigPacket();
    }
}
