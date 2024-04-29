package com.ultreon.devices.forge;

import com.ultreon.devices.BuiltinApps;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;

public class BuiltinAppsRegistration {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerBuiltinApps(NeoForgeApplicationRegistration event) {
        BuiltinApps.registerBuiltinApps();
    }
}
