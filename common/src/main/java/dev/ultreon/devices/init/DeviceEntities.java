package dev.ultreon.devices.init;

import dev.ultreon.devices.UltreonDevicesMod;
import dev.ultreon.devices.entity.SeatEntity;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class DeviceEntities {
    private static final Registrar<EntityType<?>> REGISTER = UltreonDevicesMod.REGISTRIES.get().get(Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<SeatEntity>> SEAT = REGISTER.register(UltreonDevicesMod.id("seat"), () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC).sized(0.5f, 1.975f).clientTrackingRange(10).noSummon().build(UltreonDevicesMod.id("seat").toString()));

    public static void register() {

    }
}
