package com.ultreon.devices.entity;

import com.ultreon.devices.init.DeviceEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class SeatEntity extends Entity
{
    private double yOffset;
    public SeatEntity(EntityType<SeatEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.setBoundingBox(new AABB(0.001F, 0.001F, 0.001F, -0.001F, -0.001F, -0.001F));
        this.setInvisible(true);
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0;
    }

    public SeatEntity(Level worldIn, BlockPos pos, double yOffset)
    {
        this(DeviceEntities.SEAT.get(), worldIn);
        this.setPos(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
    }


    public void setYOffset(double offset) {
        this.yOffset = offset;
    }

    public void setViaYOffset(BlockPos pos) {
        this.setPos(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
    }



//    @Override
//    protected boolean shouldSetPosAfterLoading()
//    {
//        return false;
//    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick()
    {
        if(!this.level.isClientSide && (!this.hasExactlyOnePlayerPassenger() || this.level.isEmptyBlock(this.getOnPos())))
        {
            this.kill();
        }
    }


    @Nullable
    public Entity getControllingPassenger()
    {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

//    @Override
//    protected void init() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}
}
