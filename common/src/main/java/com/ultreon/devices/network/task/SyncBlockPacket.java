package com.ultreon.devices.network.task;

import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.network.Packet;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author MrCrayfish
 */
public class SyncBlockPacket extends Packet<SyncBlockPacket> {
    private final BlockPos routerPos;

    public SyncBlockPacket(PacketBuffer buf) {
        this.routerPos = buf.readBlockPos();
    }

    public SyncBlockPacket(BlockPos routerPos) {
        this.routerPos = routerPos;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(routerPos);
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        World level = Objects.requireNonNull(ctx.get().getSender()).level;
        TileEntity blockEntity = level.getChunkAt(routerPos).getBlockEntity(routerPos, Chunk.CreateEntityType.IMMEDIATE);
        if (blockEntity instanceof RouterBlockEntity router) {
            router.syncDevicesToClient();
        }
        return true;
    }
}
