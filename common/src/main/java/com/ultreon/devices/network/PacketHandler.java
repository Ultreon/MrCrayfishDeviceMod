package com.ultreon.devices.network;

import com.ultreon.devices.core.laptop.common.C2SUpdatePacket;
import com.ultreon.devices.core.laptop.common.S2CUpdatePacket;
import com.ultreon.devices.network.task.*;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {
    private static int id = 0;
    private static CustomPacketPayload.Type<RequestPacket> requestPacket;
    private static CustomPacketPayload.Type<ResponsePacket> responsePacket;
    private static CustomPacketPayload.Type<SyncApplicationPacket> syncApplicationPacket;
    private static CustomPacketPayload.Type<SyncConfigPacket> syncConfigPacket;
    private static CustomPacketPayload.Type<SyncBlockPacket> syncBlockPacket;
    private static CustomPacketPayload.Type<NotificationPacket> notificationPacket;
    private static CustomPacketPayload.Type<S2CUpdatePacket> s2cUpdatePacket;
    private static CustomPacketPayload.Type<C2SUpdatePacket> c2sUpdatePacket;

    public static CustomPacketPayload.Type<RequestPacket> getRequestPacket() {
        return requestPacket;
    }

    public static CustomPacketPayload.Type<ResponsePacket> getResponsePacket() {
        return responsePacket;
    }

    public static CustomPacketPayload.Type<SyncApplicationPacket> getSyncApplicationPacket() {
        return syncApplicationPacket;
    }

    public static CustomPacketPayload.Type<SyncConfigPacket> getSyncConfigPacket() {
        return syncConfigPacket;
    }

    public static CustomPacketPayload.Type<SyncBlockPacket> getSyncBlockPacket() {
        return syncBlockPacket;
    }

    public static CustomPacketPayload.Type<NotificationPacket> getNotificationPacket() {
        return notificationPacket;
    }

    public static CustomPacketPayload.Type<S2CUpdatePacket> getS2CUpdatePacket() {
        return s2cUpdatePacket;
    }

    public static CustomPacketPayload.Type<C2SUpdatePacket> getC2SUpdatePacket() {
        return c2sUpdatePacket;
    }

    public static void init() {
        requestPacket = registerC2S(RequestPacket.class, Packet::toBytes, RequestPacket::new, RequestPacket::onMessage);
        responsePacket = registerS2C(ResponsePacket.class, Packet::toBytes, ResponsePacket::new, ResponsePacket::onMessage);
        syncApplicationPacket = registerS2C(SyncApplicationPacket.class, Packet::toBytes, SyncApplicationPacket::new, SyncApplicationPacket::onMessage);
        syncConfigPacket = registerS2C(SyncConfigPacket.class, Packet::toBytes, SyncConfigPacket::new, SyncConfigPacket::onMessage);
        syncBlockPacket = registerS2C(SyncBlockPacket.class, Packet::toBytes, SyncBlockPacket::new, SyncBlockPacket::onMessage);
        notificationPacket = registerS2C(NotificationPacket.class, Packet::toBytes, NotificationPacket::new, NotificationPacket::onMessage);
        s2cUpdatePacket = registerS2C(S2CUpdatePacket.class, Packet::toBytes, S2CUpdatePacket::new, S2CUpdatePacket::onMessage);
        c2sUpdatePacket = registerC2S(C2SUpdatePacket.class, Packet::toBytes, C2SUpdatePacket::new, C2SUpdatePacket::onMessage);
    }

    public static <T extends Packet<T>> CustomPacketPayload.Type<T> registerC2S(Class<T> packetClass, BiConsumer<T, RegistryFriendlyByteBuf> toBytes, Function<RegistryFriendlyByteBuf, T> fromBytes, BiConsumer<T, Supplier<NetworkManager.PacketContext>> onMessage) {
        CustomPacketPayload.Type<T> id1 = nextId();
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, id1, StreamCodec.of((buf, v) -> toBytes.accept(v, buf), fromBytes::apply), (T value, NetworkManager.PacketContext context) -> onMessage.accept(value, () -> context));
        return id1;
    }

    public static <T extends Packet<T>> CustomPacketPayload.Type<T> registerS2C(Class<T> packetClass, BiConsumer<T, RegistryFriendlyByteBuf> toBytes, Function<RegistryFriendlyByteBuf, T> fromBytes, BiConsumer<T, Supplier<NetworkManager.PacketContext>> onMessage) {
        CustomPacketPayload.Type<T> id1 = nextId();
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, id1, StreamCodec.of((buf, v) -> toBytes.accept(v, buf), fromBytes::apply), (T value, NetworkManager.PacketContext context) -> onMessage.accept(value, () -> context));
        return id1;
    }

    private static <T extends Packet<T>> CustomPacketPayload.Type<T> nextId() {
        return new CustomPacketPayload.Type<>(ResourceLocation.parse("devices:generated/id" + id++));
    }

    @Environment(EnvType.CLIENT)
    public static <T extends Packet<T>> void sendToServer(T message) {
        if (Minecraft.getInstance().getConnection() != null) {
            NetworkManager.sendToServer(message);
        } else {
            throw new IllegalArgumentException("Connection is null");
        }
    }

    public static <T extends Packet<T>> void sendToClient(Packet<T> messageNotification, Player player) { // has to be ServerPlayer if world is not null
        if (player == null) {
            throw new IllegalArgumentException("Player is null");
        } else {
            player.level();
        }
        NetworkManager.sendToPlayer((ServerPlayer) player, messageNotification);
    }
}
