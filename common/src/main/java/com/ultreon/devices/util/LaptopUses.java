package com.ultreon.devices.util;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.IntConsumer;

public class LaptopUses {
    private static final Reference2IntMap<UUID> usageTicks = new Reference2IntArrayMap<>();

    public static void stopUsing(ServerPlayer player) {
        Preconditions.checkNotNull(player, "The 'player' argument is null.");
        usageTicks.removeInt(player.getUUID());
    }

    public static void startUsing(ServerPlayer player) {
        Preconditions.checkNotNull(player, "The 'player' argument is null.");
        usageTicks.put(player.getUUID(), 0);
    }

    public static void tick(ServerPlayer player) {
        Preconditions.checkNotNull(player, "The 'player' argument is null.");
        tick(player, value -> {});
    }

    public static void tick(Player player, IntConsumer tickHandler) {
        usageTicks.computeIntIfPresent(player.getUUID(), (uuid, ticks) -> {
            ticks++;
            tickHandler.accept(ticks);
            return ticks;
        });
    }
}
