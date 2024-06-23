package com.ultreon.devices.block.entity;

import com.ultreon.devices.annotations.PlatformOverride;
import com.ultreon.devices.util.BlockEntityUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.jetbrains.annotations.NotNull;
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
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    @PlatformOverride("forge")
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.load(getBlockState(), Objects.requireNonNull(pkt.getTag(), "The data packet for the block entity contained no data"));
    }

    @Override
    public @NotNull CompoundNBT getUpdateTag() {
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
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getBlockPos(), -1, getUpdateTag());
    }

    public CompoundNBT getPipeline() {
        return pipeline;
    }
}
