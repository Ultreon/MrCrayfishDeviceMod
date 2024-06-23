package com.ultreon.devices.block.entity;

import com.ultreon.devices.annotations.PlatformOverride;
import com.ultreon.devices.util.BlockEntityUtil;
import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.SUpdateTileEntityPacket;
import net.minecraft.item.DebugStickItem;
import net.minecraft.block.DoorBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class SyncBlockEntity extends TileEntity {
    protected CompoundNBT pipeline = new CompoundNBT();

    public SyncBlockEntity(TileEntityType<?> pType) {
        super(pType);
    }

    public void sync() {
        assert level != null;
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    // from SignBlockEntity
    protected void markUpdated() {
        this.setChanged();
        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @PlatformOverride("forge")
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.load(Objects.requireNonNull(pkt.getTag(), "The data packet for the block entity contained no data"));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        if (!pipeline.isEmpty()) {
            CompoundNBT updateTag = pipeline;
            save(updateTag);
            pipeline = new CompoundNBT();
            return updateTag;
        }
        CompoundNBT updateTag = saveSyncTag();
        super.save(updateTag);
        return updateTag;
    }

    public abstract CompoundNBT saveSyncTag();

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return SUpdateTileEntityPacket.create(this, TileEntity::getUpdateTag);
    }

    public CompoundNBT getPipeline() {
        return pipeline;
    }
}
