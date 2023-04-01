package com.ultreon.devices.util;

import com.ultreon.devices.entity.SeatEntity;
import com.ultreon.devices.init.DeviceEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SeatUtil
{
    public static void createSeatAndSit(Level worldIn, BlockPos pos, Player playerIn, double yOffset)
    {
        List<SeatEntity> seats = worldIn.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
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