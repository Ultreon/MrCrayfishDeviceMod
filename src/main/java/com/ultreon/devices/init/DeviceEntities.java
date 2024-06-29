package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.entity.SeatEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DeviceEntities {
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, Devices.MOD_ID);

    public static final RegistryObject<EntityType<SeatEntity>> SEAT = REGISTER.register("seat", () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, EntityClassification.MISC).sized(0.5f, 1.975f).clientTrackingRange(10).noSummon().build(Devices.id("seat").toString()));


    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
