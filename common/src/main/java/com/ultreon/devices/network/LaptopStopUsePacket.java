package com.ultreon.devices.network;

import com.google.common.base.Preconditions;
import com.ultreon.devices.util.LaptopUses;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public class LaptopStopUsePacket extends Packet<LaptopStopUsePacket> {
    public LaptopStopUsePacket(FriendlyByteBuf buf) {
    }

    public LaptopStopUsePacket() {

    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {

    }

    @Override
    public boolean onMessage(Supplier<NetworkManager.PacketContext> ctx) {
        Player player = ctx.get().getPlayer();
        Preconditions.checkNotNull(player, "Couldn't find the player that sent the packet.");
        if (player instanceof ServerPlayer serverPlayer) {
            LaptopUses.stopUsing(serverPlayer);
        }
        return true;
    }
}
