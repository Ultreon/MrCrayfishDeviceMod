package dev.ultreon.devices.network.task;

import dev.architectury.networking.NetworkManager;
import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.network.Packet;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author MrCrayfish
 */
public class SyncConfigPacket extends Packet<SyncConfigPacket> {
    public SyncConfigPacket() {

    }

    public SyncConfigPacket(FriendlyByteBuf buf) {
        DeviceConfig.readSyncTag(Objects.requireNonNull(buf.readNbt()));
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(DeviceConfig.writeSyncTag());
    }

    @Override
    public boolean onMessage(Supplier<NetworkManager.PacketContext> ctx) {
        return true;
    }
}
