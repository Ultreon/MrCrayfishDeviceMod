package com.ultreon.devices.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.world.flag.FeatureFlagRegistry;

public interface FeatureFlagRegisterEvent {
    Event<FeatureFlagRegisterEvent> EVENT = EventFactory.createLoop();

    void onRegisterFeatureFlags(FeatureFlagRegistry.Builder builder);
}