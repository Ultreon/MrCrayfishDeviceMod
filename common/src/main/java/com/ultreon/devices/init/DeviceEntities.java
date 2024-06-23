package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.entity.SeatEntity;
import dev.architectury.registry.registries.Registrar;
import net.minecraftforge.fml.RegistryObject;
import net.minecraft.util.Registry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobCategory;

public class DeviceEntities {
    private static final Registrar<EntityType<?>> REGISTER = Devices.REGISTRIES.get().get(Registry.ENTITY_TYPE_REGISTRY);

    public static final RegistryObject<EntityType<SeatEntity>> SEAT = REGISTER.register(Devices.id("seat"), () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC).sized(0.5f, 1.975f).clientTrackingRange(10).noSummon().build(Devices.id("seat").toString()));

    public static void register() {

    }
}
