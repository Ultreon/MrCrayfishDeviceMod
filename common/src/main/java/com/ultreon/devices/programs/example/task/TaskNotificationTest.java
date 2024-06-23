package com.ultreon.devices.programs.example.task;

import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Notification;
import com.ultreon.devices.api.task.Task;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * @author MrCrayfish
 */
public class TaskNotificationTest extends Task {
    public TaskNotificationTest() {
        super("notification_test");
    }

    @Override
    public void prepareRequest(CompoundNBT nbt) {

    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player) {
        Notification notification = new Notification(Icons.MAIL, "New Email!", "Check your inbox");
        notification.pushTo((ServerPlayerEntity) player);

       /* MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        List<EntityPlayerMP> players = server.getPlayerList().getPlayers();
        players.forEach(notification::pushTo);*/
    }

    @Override
    public void prepareResponse(CompoundNBT nbt) {

    }

    @Override
    public void processResponse(CompoundNBT nbt) {

    }
}
