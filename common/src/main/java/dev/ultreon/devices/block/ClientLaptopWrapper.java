package dev.ultreon.devices.block;

import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.client.Display;
import dev.ultreon.devices.mineos.client.MineOS;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ClientLaptopWrapper {

    public static void execute(Player player, ComputerBlockEntity laptop) {
        if (!laptop.isPoweredOn()) {
            player.displayClientMessage(Component.translatable("message.devices.computer.not_powered"), true);
            return;
        }
        Minecraft.getInstance().setScreen(Display.open(laptop));
    }
}
