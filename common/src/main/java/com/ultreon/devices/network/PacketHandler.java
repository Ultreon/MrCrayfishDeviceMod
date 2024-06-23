package com.ultreon.devices.network;

import com.ultreon.devices.Devices;
import com.ultreon.devices.core.laptop.common.C2SUpdatePacket;
import com.ultreon.devices.core.laptop.common.S2CUpdatePacket;
import com.ultreon.devices.network.task.*;
import dev.architectury.networking.NetworkChannel;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class PacketHandler {
    public static final NetworkChannel INSTANCE = NetworkChannel.create(Devices.id("main_channel"));
    private static int id = 0;

    public static void init() {
        INSTANCE.register(RequestPacket.class, RequestPacket::toBytes, RequestPacket::new, RequestPacket::onMessage);
        INSTANCE.register(ResponsePacket.class, Packet::toBytes, ResponsePacket::new, ResponsePacket::onMessage);
        INSTANCE.register(SyncApplicationPacket.class, Packet::toBytes, SyncApplicationPacket::new, SyncApplicationPacket::onMessage);
        INSTANCE.register(SyncConfigPacket.class, Packet::toBytes, SyncConfigPacket::new, SyncConfigPacket::onMessage);
        INSTANCE.register(SyncBlockPacket.class, Packet::toBytes, SyncBlockPacket::new, SyncBlockPacket::onMessage);
        INSTANCE.register(NotificationPacket.class, Packet::toBytes, NotificationPacket::new, NotificationPacket::onMessage);
        INSTANCE.register(S2CUpdatePacket.class, Packet::toBytes, S2CUpdatePacket::new, S2CUpdatePacket::onMessage);
        INSTANCE.register(C2SUpdatePacket.class, Packet::toBytes, C2SUpdatePacket::new, C2SUpdatePacket::onMessage);
    }

    private static int nextId() {
        return id++;
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends Packet<T>> void sendToServer(T message) {
        if (Minecraft.getInstance().getConnection() != null) {
            INSTANCE.sendToServer(message);
        } else {
            Minecraft.getInstance().doRunTask(() ->
            message.onMessage(() -> new NetworkEvent.Context() {

                @Override
                public PlayerEntity getPlayer() {
                    return Minecraft.getInstance().player;
                }

                @Override
                public void queue(Runnable runnable) {

                }

                @Override
                public Dist getOnlyIn() {
                    return Dist.SERVER;
                }
            }));
        }
    }

    public static <T extends Packet<T>> void sendToClient(Packet<T> messageNotification, PlayerEntity player) { // has to be ServerPlayer if world is not null
        if (player == null || player.level == null) {
            messageNotification.onMessage(() -> new NetworkEvent.Context() {
                @Override
                public PlayerEntity getPlayer() {
                    return player;
                }

                @Override
                public void queue(Runnable runnable) {

                }

                @Override
                public Dist getOnlyIn() {
                    return Dist.CLIENT;
                }
            });
            return;
        }
        INSTANCE.sendToPlayer((ServerPlayerEntity) player, messageNotification);
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
