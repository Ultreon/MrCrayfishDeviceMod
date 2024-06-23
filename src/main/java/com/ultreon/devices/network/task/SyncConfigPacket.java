package com.ultreon.devices.network.task;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.network.Packet;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraft.network.PacketBuffer;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author MrCrayfish
 */
public class SyncConfigPacket extends Packet<SyncConfigPacket> {
    public SyncConfigPacket() {

    }

    public SyncConfigPacket(PacketBuffer buf) {
        DeviceConfig.readSyncTag(Objects.requireNonNull(buf.readNbt()));
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeNbt(DeviceConfig.writeSyncTag());
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        return true;
    }
}
