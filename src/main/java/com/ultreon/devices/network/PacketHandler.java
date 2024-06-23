package com.ultreon.devices.network;

import com.ultreon.devices.Devices;
import com.ultreon.devices.core.laptop.common.C2SUpdatePacket;
import com.ultreon.devices.core.laptop.common.S2CUpdatePacket;
import com.ultreon.devices.network.task.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Devices.MOD_ID, "main_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int id = 0;

    public static void init() {
        INSTANCE.registerMessage(nextId(), RequestPacket.class, RequestPacket::toBytes, RequestPacket::new, RequestPacket::onMessage);
        INSTANCE.registerMessage(nextId(), ResponsePacket.class, Packet::toBytes, ResponsePacket::new, ResponsePacket::onMessage);
        INSTANCE.registerMessage(nextId(), SyncApplicationPacket.class, Packet::toBytes, SyncApplicationPacket::new, SyncApplicationPacket::onMessage);
        INSTANCE.registerMessage(nextId(), SyncConfigPacket.class, Packet::toBytes, SyncConfigPacket::new, SyncConfigPacket::onMessage);
        INSTANCE.registerMessage(nextId(), SyncBlockPacket.class, Packet::toBytes, SyncBlockPacket::new, SyncBlockPacket::onMessage);
        INSTANCE.registerMessage(nextId(), NotificationPacket.class, Packet::toBytes, NotificationPacket::new, NotificationPacket::onMessage);
        INSTANCE.registerMessage(nextId(), S2CUpdatePacket.class, Packet::toBytes, S2CUpdatePacket::new, S2CUpdatePacket::onMessage);
        INSTANCE.registerMessage(nextId(), C2SUpdatePacket.class, Packet::toBytes, C2SUpdatePacket::new, C2SUpdatePacket::onMessage);
    }

    private static int nextId() {
        return id++;
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends Packet<T>> void sendToServer(T message) {
        if (Minecraft.getInstance().getConnection() != null) {
            INSTANCE.sendToServer(message);
        }
    }

    public static <T extends Packet<T>> void sendToClient(Packet<T> messageNotification, PlayerEntity player) { // has to be ServerPlayer if world is not null
        INSTANCE.sendTo(messageNotification, ((ServerPlayerEntity) player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        //INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), messageNotification);
    }

    // seems to be unused
    public static <T extends Packet<T>> void sendToDimension(Packet<T> messageNotification, RegistryKey<World> level) {
        //INSTANCE.sendToServer();
        //INSTANCE.send(PacketDistributor.DIMENSION.with(() -> level), messageNotification);
    }

//    public static <T extends Packet<T>> void sendToDimension(Packet<T> messageNotification, World level) {
//        INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToServer(Packet<T> messageNotification) {
//        INSTANCE.send(PacketDistributor.SERVER.noArg(), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToAll(Packet<T> messageNotification) {
//        INSTANCE.send(PacketDistributor.ALL.noArg(), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToAllAround(Packet<T> messageNotification, ResourceKey<World> level, double x, double y, double z, double radius) {
//        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, level)), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToAllAround(Packet<T> messageNotification, World level, double x, double y, double z, double radius) {
//        INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, level.dimension())), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToTrackingChunk(Packet<T> messageNotification, LevelChunk chunk) {
//        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToTrackingChunk(Packet<T> messageNotification, World level, int x, int z) {
//        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunk(x, z)), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToTrackingEntity(Packet<T> messageNotification, Entity entity) {
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToTrackingEntity(Packet<T> messageNotification, World level, int entityId) {
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> level.getEntity(entityId)), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToTrackingEntityAndSelf(Packet<T> messageNotification, Entity entity) {
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), messageNotification);
//    }
//
//    public static <T extends Packet<T>> void sendToTrackingEntityAndSelf(Packet<T> messageNotification, World level, int entityId) {
//        INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> level.getEntity(entityId)), messageNotification);
//    }
}
