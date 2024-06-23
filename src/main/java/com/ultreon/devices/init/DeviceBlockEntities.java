package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.entity.*;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public class DeviceBlockEntities {
    private static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Devices.MOD_ID);

    public static final RegistryObject<TileEntityType<PaperBlockEntity>> PAPER = REGISTER.register("paper", () -> TileEntityType.Builder.of(PaperBlockEntity::new, DeviceBlocks.PAPER.get()).build(null));
    public static final RegistryObject<TileEntityType<LaptopBlockEntity>> LAPTOP = REGISTER.register("laptop", () -> TileEntityType.Builder.of(LaptopBlockEntity::new, DeviceBlocks.getAllLaptops().toArray(new Block[]{})).build(null));
    public static final RegistryObject<TileEntityType<PrinterBlockEntity>> PRINTER = REGISTER.register("printer", () -> TileEntityType.Builder.of(PrinterBlockEntity::new, DeviceBlocks.getAllPrinters().toArray(new Block[]{})).build(null));
    public static final RegistryObject<TileEntityType<RouterBlockEntity>> ROUTER = REGISTER.register("router", () -> TileEntityType.Builder.of(RouterBlockEntity::new, DeviceBlocks.getAllRouters().toArray(new Block[]{})).build(null));
    public static final RegistryObject<TileEntityType<OfficeChairBlockEntity>> SEAT = REGISTER.register("seat", () -> TileEntityType.Builder.of(OfficeChairBlockEntity::new, DeviceBlocks.getAllOfficeChairs().toArray(new Block[]{})).build(null));

    public static void register() {
   //    Marker
    }
}
