package com.ultreon.devices.init;

import com.google.common.collect.Lists;
import com.ultreon.devices.Devices;
import com.ultreon.devices.block.*;
import com.ultreon.devices.util.DyeableRegistration;
import dev.ultreon.mods.xinexlib.platform.services.IRegistrar;
import dev.ultreon.mods.xinexlib.platform.services.IRegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.stream.Stream;

public class DeviceBlocks {
    private static final IRegistrar<Block> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.BLOCK);

    public static void register() {
    }

    public static final DyeableRegistration<LaptopBlock, Block> LAPTOPS = new DyeableRegistration<>() {
        @Override
        public IRegistrySupplier<LaptopBlock, Block> register(IRegistrar<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_laptop", () -> new LaptopBlock(color));
        }

        @Override
        protected IRegistrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final IRegistrySupplier<MacMaxXBlock, Block> MAC_MAX_X = REGISTER.register("mac_max_x", MacMaxXBlock::new);
    public static final IRegistrySupplier<MacMaxXBlockPart, Block> MAC_MAX_X_PART = REGISTER.register("mac_max_x_part", MacMaxXBlockPart::new);

    public static final DyeableRegistration<PrinterBlock, Block> PRINTERS = new DyeableRegistration<>() {
        @Override
        public IRegistrySupplier<PrinterBlock, Block> register(IRegistrar<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_printer", () -> new PrinterBlock(color));
        }

        @Override
        protected IRegistrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<RouterBlock, Block> ROUTERS = new DyeableRegistration<>() {
        @Override
        public IRegistrySupplier<RouterBlock, Block> register(IRegistrar<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_router", () -> new RouterBlock(color));
        }

        @Override
        protected IRegistrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<OfficeChairBlock, Block> OFFICE_CHAIRS = new DyeableRegistration<>() {
        @Override
        public IRegistrySupplier<OfficeChairBlock, Block> register(IRegistrar<Block> registrar, DyeColor color) {
            return registrar.register(color.getName() + "_office_chair", () -> new OfficeChairBlock(color));
        }

        @Override
        protected IRegistrar<Block> autoInit() {
            return REGISTER;
        }
    };


    public static final IRegistrySupplier<PaperBlock, Block> PAPER = REGISTER.register("paper", PaperBlock::new);


    public static Stream<Block> getAllBlocks() {
        return Lists.newArrayList(REGISTER).stream().map(IRegistrySupplier::get);
    }

    public static List<LaptopBlock> getAllLaptops() {
        return getAllBlocks().filter(block -> block instanceof LaptopBlock).map(block -> (LaptopBlock) block).toList();
    }

    public static List<PrinterBlock> getAllPrinters() {
        return getAllBlocks().filter(block -> block instanceof PrinterBlock).map(block -> (PrinterBlock) block).toList();
    }

    public static List<RouterBlock> getAllRouters() {
        return getAllBlocks().filter(block -> block instanceof RouterBlock).map(block -> (RouterBlock) block).toList();
    }

    public static List<OfficeChairBlock> getAllOfficeChairs() {
        return getAllBlocks().filter(block -> block instanceof OfficeChairBlock).map(block -> (OfficeChairBlock) block).toList();
    }
}
