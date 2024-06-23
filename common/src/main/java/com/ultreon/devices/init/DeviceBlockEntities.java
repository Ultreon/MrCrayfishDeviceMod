package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.entity.*;
import dev.architectury.registry.registries.Registrar;
import net.minecraftforge.fml.RegistryObject;
import net.minecraft.util.Registry;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;

@SuppressWarnings("ConstantConditions")
public class DeviceBlockEntities {
    private static final Registrar<TileEntityType<?>> REGISTER = Devices.REGISTRIES.get().get(Registry.BLOCK_ENTITY_TYPE_REGISTRY);

    public static final RegistryObject<TileEntityType<PaperBlockEntity>> PAPER = REGISTER.register(Devices.id("paper"), () -> TileEntityType.Builder.of(PaperBlockEntity::new, DeviceBlocks.PAPER.get()).build(null));
    public static final RegistryObject<TileEntityType<LaptopBlockEntity>> LAPTOP = REGISTER.register(Devices.id("laptop"), () -> TileEntityType.Builder.of(LaptopBlockEntity::new, DeviceBlocks.getAllLaptops().toArray(new Block[]{})).build(null));
    public static final RegistryObject<TileEntityType<PrinterBlockEntity>> PRINTER = REGISTER.register(Devices.id("printer"), () -> TileEntityType.Builder.of(PrinterBlockEntity::new, DeviceBlocks.getAllPrinters().toArray(new Block[]{})).build(null));
    public static final RegistryObject<TileEntityType<RouterBlockEntity>> ROUTER = REGISTER.register(Devices.id("router"), () -> TileEntityType.Builder.of(RouterBlockEntity::new, DeviceBlocks.getAllRouters().toArray(new Block[]{})).build(null));
    public static final RegistryObject<TileEntityType<OfficeChairBlockEntity>> SEAT = REGISTER.register(Devices.id("seat"), () -> TileEntityType.Builder.of(OfficeChairBlockEntity::new, DeviceBlocks.getAllOfficeChairs().toArray(new Block[]{})).build(null));

    public static void register() {
   //    Marker
    }
}
