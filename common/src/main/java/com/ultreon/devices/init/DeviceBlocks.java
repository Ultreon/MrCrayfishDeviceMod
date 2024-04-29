package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.*;
import com.ultreon.devices.util.DyeableRegistration;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import java.util.List;
import java.util.stream.Stream;

public class DeviceBlocks {
    private static final Registrar<Block> REGISTER = Devices.REGISTRIES.get().get(Registries.BLOCK);

    public static void register() {
    }

    public static final DyeableRegistration<Block> LAPTOPS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_laptop"), () -> new LaptopBlock(color, Properties.of().mapColor(color).strength(6f).sound(SoundType.METAL)));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final RegistrySupplier<MacMaxXBlock> MAC_MAX_X = REGISTER.register(Devices.id("mac_max_x"), () -> new MacMaxXBlock(Properties.of().mapColor(DyeColor.WHITE).strength(6f).sound(SoundType.METAL).noOcclusion().dynamicShape()));
    public static final RegistrySupplier<MacMaxXBlockPart> MAC_MAX_X_PART = REGISTER.register(Devices.id("mac_max_x_part"), () -> new MacMaxXBlockPart(Properties.of().mapColor(DyeColor.WHITE).strength(6f).sound(SoundType.METAL)));

    public static final DyeableRegistration<Block> PRINTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_printer"), () -> new PrinterBlock(color, Properties.of().mapColor(color).strength(6f).sound(SoundType.METAL)));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<Block> ROUTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_router"), () -> new RouterBlock(color, Properties.of().mapColor(color).strength(6f).sound(SoundType.METAL)));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };

    public static final DyeableRegistration<Block> OFFICE_CHAIRS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Block> register(Registrar<Block> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_office_chair"), () -> new OfficeChairBlock(color, BlockBehaviour.Properties.of().mapColor(color)));
        }

        @Override
        protected Registrar<Block> autoInit() {
            return REGISTER;
        }
    };


    public static final RegistrySupplier<PaperBlock> PAPER = REGISTER.register(Devices.id("paper"), () -> new PaperBlock(Properties.of().noCollission().instabreak().noOcclusion().noLootTable()));


    public static Stream<Block> getAllBlocks() {
        return REGISTER.getIds().stream().map(REGISTER::get);
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
