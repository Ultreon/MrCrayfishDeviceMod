package com.ultreon.devices.network.task;

import com.ultreon.devices.Devices;
import com.ultreon.devices.api.app.Notification;
import com.ultreon.devices.network.Packet;
import com.ultreon.devices.network.PacketHandler;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/// @author MrCrayfish
public class NotificationPacket extends Packet<NotificationPacket> {
    private final CompoundTag notificationTag;

    public NotificationPacket(RegistryFriendlyByteBuf buf) {
        notificationTag = buf.readNbt();
    }

    public NotificationPacket(Notification notification) {
        this.notificationTag = notification.toTag();
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(notificationTag);
    }

    @Override
    public boolean onMessage(Supplier<NetworkManager.PacketContext> ctx) {
        Devices.showNotification(notificationTag);
        return true;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PacketHandler.getNotificationPacket();
    }
}
