package com.ultreon.devices.entity;

import com.ultreon.devices.init.DeviceEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

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
        if(!this.level().isClientSide && (!this.hasExactlyOnePlayerPassenger() || this.level().isEmptyBlock(this.getOnPos())))
        {
            this.kill();
        }
    }


    public LivingEntity getControllingPassenger()
    {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0) instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

//    @Override
//    protected void.json init() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}
}
