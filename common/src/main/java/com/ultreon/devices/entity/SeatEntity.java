package com.ultreon.devices.entity;

import com.ultreon.devices.init.DeviceEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.SSpawnObjectPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.decoration.ArmorStand;
import net.minecraft.entity.vehicle.Boat;
import net.minecraft.entity.vehicle.Minecart;
import net.minecraft.world.World;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nullable;
import java.util.List;

public class SeatEntity extends Entity
{
    private double yOffset;
    public SeatEntity(EntityType<SeatEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setBoundingBox(new AxisAlignedBB(0.001F, 0.001F, 0.001F, -0.001F, -0.001F, -0.001F));
        this.setInvisible(true);
    }

    @Override
    protected float getEyeHeight(Pose pose, EntitySize dimensions) {
        return 0;
    }

    public SeatEntity(World worldIn, BlockPos pos, double yOffset)
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
    public Packet<?> getAddEntityPacket() {
        return new SSpawnObjectPacket(this);
    }

//    @Override
//    protected void init() {}

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {}

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {}
}
