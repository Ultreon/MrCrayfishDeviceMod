package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.entity.Seat;
import dev.ultreon.mods.xinexlib.platform.services.IRegistrar;
import dev.ultreon.mods.xinexlib.platform.services.IRegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class DeviceEntities {
    private static final IRegistrar<EntityType<?>> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.ENTITY_TYPE);

    public static final IRegistrySupplier<EntityType<Seat>, EntityType<?>> SEAT = REGISTER.register("seat", () -> EntityType.Builder.<Seat>of(Seat::new, MobCategory.MISC).sized(0.5f, 1.975f).clientTrackingRange(10).noSummon().build(Devices.id("seat").toString()));

    public static void register() {

    }
}
