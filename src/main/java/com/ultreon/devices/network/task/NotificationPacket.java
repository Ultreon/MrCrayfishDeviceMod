package com.ultreon.devices.network.task;

import com.ultreon.devices.Devices;
import com.ultreon.devices.api.app.Notification;
import com.ultreon.devices.network.Packet;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.util.function.Supplier;

/**
 * @author MrCrayfish
 */
public class NotificationPacket extends Packet<NotificationPacket> {
    private final CompoundNBT notificationTag;

    public NotificationPacket(PacketBuffer buf) {
        notificationTag = buf.readNbt();
    }

    public NotificationPacket(Notification notification) {
        this.notificationTag = notification.toTag();
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeNbt(notificationTag);
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        Devices.showNotification(notificationTag);
        return true;
    }
}
