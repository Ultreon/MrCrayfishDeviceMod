package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.entity.*;
import dev.ultreon.mods.xinexlib.platform.services.IRegistrar;
import dev.ultreon.mods.xinexlib.platform.services.IRegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

@SuppressWarnings("ConstantConditions")
public class DeviceBlockEntities {
    private static final IRegistrar<BlockEntityType<?>> REGISTER = Devices.REGISTRIES.get().get(Registries.BLOCK_ENTITY_TYPE);

    public static final IRegistrySupplier<BlockEntityType<PaperBlockEntity>, BlockEntityType<?>> PAPER = REGISTER.register("paper", () -> BlockEntityType.Builder.of(PaperBlockEntity::new, DeviceBlocks.PAPER.get()).build(null));
    public static final IRegistrySupplier<BlockEntityType<LaptopBlockEntity>, BlockEntityType<?>> LAPTOP = REGISTER.register("laptop", () -> BlockEntityType.Builder.of(LaptopBlockEntity::new, DeviceBlocks.getAllLaptops().toArray(new Block[]{})).build(null));
    public static final IRegistrySupplier<BlockEntityType<MacMaxXBlockEntity>, BlockEntityType<?>> MAC_MAX_X = REGISTER.register("mac_max_x", () -> BlockEntityType.Builder.of(MacMaxXBlockEntity::new, DeviceBlocks.MAC_MAX_X.get()).build(null));
    public static final IRegistrySupplier<BlockEntityType<PrinterBlockEntity>, BlockEntityType<?>> PRINTER = REGISTER.register("printer", () -> BlockEntityType.Builder.of(PrinterBlockEntity::new, DeviceBlocks.getAllPrinters().toArray(new Block[]{})).build(null));
    public static final IRegistrySupplier<BlockEntityType<RouterBlockEntity>, BlockEntityType<?>> ROUTER = REGISTER.register("router", () -> BlockEntityType.Builder.of(RouterBlockEntity::new, DeviceBlocks.getAllRouters().toArray(new Block[]{})).build(null));
    public static final IRegistrySupplier<BlockEntityType<OfficeChairBlockEntity>, BlockEntityType<?>> SEAT = REGISTER.register("seat", () -> BlockEntityType.Builder.of(OfficeChairBlockEntity::new, DeviceBlocks.getAllOfficeChairs().toArray(new Block[]{})).build(null));

    public static void register() {
   //    Marker
    }
}
