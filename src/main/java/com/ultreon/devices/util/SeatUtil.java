package com.ultreon.devices.util;

import com.ultreon.devices.entity.SeatEntity;
import com.ultreon.devices.init.DeviceEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class SeatUtil
{
    public static void createSeatAndSit(World worldIn, BlockPos pos, PlayerEntity playerIn, double yOffset)
    {
        List<SeatEntity> seats = worldIn.getEntitiesOfClass(SeatEntity.class, new AxisAlignedBB(pos));
        if(!seats.isEmpty())
        {
            SeatEntity seat = seats.get(0);
            if(seat.getFirstPassenger() == null)
            {
                playerIn.startRiding(seat);
            }
        }
        else
        {
            SeatEntity seat = DeviceEntities.SEAT.get().create(worldIn);// new SeatEntity(worldIn, pos, yOffset);
            assert seat != null;
            seat.setYOffset(yOffset);
            seat.setViaYOffset(pos);
            System.out.println(seat);
            worldIn.addFreshEntity(seat);
            playerIn.startRiding(seat);
        }
    }
}