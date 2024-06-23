package com.ultreon.devices.util;

import com.ultreon.devices.entity.SeatEntity;
import com.ultreon.devices.init.DeviceEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.monster.Zombie;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3;

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
            SeatEntity seat = (SeatEntity) DeviceEntities.SEAT.get().create(worldIn);// new SeatEntity(worldIn, pos, yOffset);
            assert seat != null;
            seat.setYOffset(yOffset);
            seat.setViaYOffset(pos);
            System.out.println(seat);
            worldIn.addFreshEntity(seat);
            playerIn.startRiding(seat);
        }
    }
}