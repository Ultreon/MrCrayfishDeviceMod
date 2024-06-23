package com.ultreon.devices.block.entity;

import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.entity.SeatEntity;
import com.ultreon.devices.init.DeviceBlockEntities;
import com.ultreon.devices.util.Colorable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OfficeChairBlockEntity extends SyncBlockEntity implements Colorable {
    private DyeColor color = DyeColor.RED;

    public OfficeChairBlockEntity() {
        super(DeviceBlockEntities.SEAT.get());
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public void setColor(DyeColor color) {
        this.color = color;
    }

    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundNBT compound) {
        super.load(state, compound);
        if (compound.contains("color", Constants.NBT.TAG_BYTE)) {
            color = DyeColor.byId(compound.getByte("color"));
        }
    }

    @Override
    public @NotNull CompoundNBT save(@NotNull CompoundNBT compound) {
        super.save(compound);
        compound.putByte("color", (byte) color.getId());
        return compound;
    }

    @Override
    public CompoundNBT saveSyncTag() {
        CompoundNBT tag = new CompoundNBT();
        tag.putByte("color", (byte) color.getId());
        return tag;
    }

    @OnlyIn(Dist.CLIENT)
    public float getRotation() {
        List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AxisAlignedBB(getBlockPos()));
        if (!seats.isEmpty()) {
            SeatEntity seat = seats.get(0);
            if (seat.getControllingPassenger() != null) {
                if (seat.getControllingPassenger() instanceof LivingEntity) {
                    LivingEntity living = (LivingEntity) seat.getControllingPassenger();
                    //living.yHeadRotO = living.yHeadRot;
                    //living.yRotO = living.yHeadRot;
                    living.setYBodyRot(living.yHeadRot);
                    //living.renderYawOffset = living.rotationYaw;
                    //living.prevRenderYawOffset = living.rotationYaw;
                    return living.yHeadRot;
                }
                return seat.getControllingPassenger().getYHeadRot();
            }
        }
        float direction = this.getBlockState().getValue(LaptopBlock.FACING).getClockWise().toYRot();
        return direction + 180F;
    }
}