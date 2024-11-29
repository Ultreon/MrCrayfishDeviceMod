package com.ultreon.devices.network.task;

import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.network.Packet;
import com.ultreon.devices.network.PacketHandler;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/// @author MrCrayfish
public class SyncBlockPacket extends Packet<SyncBlockPacket> {
    private final BlockPos routerPos;

    public SyncBlockPacket(RegistryFriendlyByteBuf buf) {
        this.routerPos = buf.readBlockPos();
    }

    public SyncBlockPacket(BlockPos routerPos) {
        this.routerPos = routerPos;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(routerPos);
    }

    @Override
    public boolean onMessage(Supplier<NetworkManager.PacketContext> ctx) {
        Level level = Objects.requireNonNull(ctx.get().getPlayer()).level();
        BlockEntity blockEntity = level.getChunkAt(routerPos).getBlockEntity(routerPos, LevelChunk.EntityCreationType.IMMEDIATE);
        if (blockEntity instanceof RouterBlockEntity router) {
            router.syncDevicesToClient();
        }
        return true;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PacketHandler.getSyncBlockPacket();
    }
}
