package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.*;
import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeviceBlocks {
    private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Devices.MOD_ID);

    public static void register() {
    }

    public static final RegistryObject<LaptopBlock> WHITE_LAPTOP = REGISTER.register("white_laptop", () -> new LaptopBlock(DyeColor.WHITE));
    public static final RegistryObject<LaptopBlock> ORANGE_LAPTOP = REGISTER.register("orange_laptop", () -> new LaptopBlock(DyeColor.ORANGE));
    public static final RegistryObject<LaptopBlock> MAGENTA_LAPTOP = REGISTER.register("magenta_laptop", () -> new LaptopBlock(DyeColor.MAGENTA));
    public static final RegistryObject<LaptopBlock> LIGHT_BLUE_LAPTOP = REGISTER.register("light_blue_laptop", () -> new LaptopBlock(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<LaptopBlock> YELLOW_LAPTOP = REGISTER.register("yellow_laptop", () -> new LaptopBlock(DyeColor.YELLOW));
    public static final RegistryObject<LaptopBlock> LIME_LAPTOP = REGISTER.register("lime_laptop", () -> new LaptopBlock(DyeColor.LIME));
    public static final RegistryObject<LaptopBlock> PINK_LAPTOP = REGISTER.register("pink_laptop", () -> new LaptopBlock(DyeColor.PINK));
    public static final RegistryObject<LaptopBlock> GRAY_LAPTOP = REGISTER.register("gray_laptop", () -> new LaptopBlock(DyeColor.GRAY));
    public static final RegistryObject<LaptopBlock> LIGHT_GRAY_LAPTOP = REGISTER.register("light_gray_laptop", () -> new LaptopBlock(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<LaptopBlock> CYAN_LAPTOP = REGISTER.register("cyan_laptop", () -> new LaptopBlock(DyeColor.CYAN));
    public static final RegistryObject<LaptopBlock> PURPLE_LAPTOP = REGISTER.register("purple_laptop", () -> new LaptopBlock(DyeColor.PURPLE));
    public static final RegistryObject<LaptopBlock> BLUE_LAPTOP = REGISTER.register("blue_laptop", () -> new LaptopBlock(DyeColor.BLUE));
    public static final RegistryObject<LaptopBlock> BROWN_LAPTOP = REGISTER.register("brown_laptop", () -> new LaptopBlock(DyeColor.BROWN));
    public static final RegistryObject<LaptopBlock> GREEN_LAPTOP = REGISTER.register("green_laptop", () -> new LaptopBlock(DyeColor.GREEN));
    public static final RegistryObject<LaptopBlock> RED_LAPTOP = REGISTER.register("red_laptop", () -> new LaptopBlock(DyeColor.RED));
    public static final RegistryObject<LaptopBlock> BLACK_LAPTOP = REGISTER.register("black_laptop", () -> new LaptopBlock(DyeColor.BLACK));

    public static final RegistryObject<PrinterBlock> WHITE_PRINTER = REGISTER.register("white_printer", () -> new PrinterBlock(DyeColor.WHITE));
    public static final RegistryObject<PrinterBlock> ORANGE_PRINTER = REGISTER.register("orange_printer", () -> new PrinterBlock(DyeColor.ORANGE));
    public static final RegistryObject<PrinterBlock> MAGENTA_PRINTER = REGISTER.register("magenta_printer", () -> new PrinterBlock(DyeColor.MAGENTA));
    public static final RegistryObject<PrinterBlock> LIGHT_BLUE_PRINTER = REGISTER.register("light_blue_printer", () -> new PrinterBlock(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<PrinterBlock> YELLOW_PRINTER = REGISTER.register("yellow_printer", () -> new PrinterBlock(DyeColor.YELLOW));
    public static final RegistryObject<PrinterBlock> LIME_PRINTER = REGISTER.register("lime_printer", () -> new PrinterBlock(DyeColor.LIME));
    public static final RegistryObject<PrinterBlock> PINK_PRINTER = REGISTER.register("pink_printer", () -> new PrinterBlock(DyeColor.PINK));
    public static final RegistryObject<PrinterBlock> GRAY_PRINTER = REGISTER.register("gray_printer", () -> new PrinterBlock(DyeColor.GRAY));
    public static final RegistryObject<PrinterBlock> LIGHT_GRAY_PRINTER = REGISTER.register("light_gray_printer", () -> new PrinterBlock(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<PrinterBlock> CYAN_PRINTER = REGISTER.register("cyan_printer", () -> new PrinterBlock(DyeColor.CYAN));
    public static final RegistryObject<PrinterBlock> PURPLE_PRINTER = REGISTER.register("purple_printer", () -> new PrinterBlock(DyeColor.PURPLE));
    public static final RegistryObject<PrinterBlock> BLUE_PRINTER = REGISTER.register("blue_printer", () -> new PrinterBlock(DyeColor.BLUE));
    public static final RegistryObject<PrinterBlock> BROWN_PRINTER = REGISTER.register("brown_printer", () -> new PrinterBlock(DyeColor.BROWN));
    public static final RegistryObject<PrinterBlock> GREEN_PRINTER = REGISTER.register("green_printer", () -> new PrinterBlock(DyeColor.GREEN));
    public static final RegistryObject<PrinterBlock> RED_PRINTER = REGISTER.register("red_printer", () -> new PrinterBlock(DyeColor.RED));
    public static final RegistryObject<PrinterBlock> BLACK_PRINTER = REGISTER.register("black_printer", () -> new PrinterBlock(DyeColor.BLACK));

    public static final RegistryObject<RouterBlock> WHITE_ROUTER = REGISTER.register("white_router", () -> new RouterBlock(DyeColor.WHITE));
    public static final RegistryObject<RouterBlock> ORANGE_ROUTER = REGISTER.register("orange_router", () -> new RouterBlock(DyeColor.ORANGE));
    public static final RegistryObject<RouterBlock> MAGENTA_ROUTER = REGISTER.register("magenta_router", () -> new RouterBlock(DyeColor.MAGENTA));
    public static final RegistryObject<RouterBlock> LIGHT_BLUE_ROUTER = REGISTER.register("light_blue_router", () -> new RouterBlock(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<RouterBlock> YELLOW_ROUTER = REGISTER.register("yellow_router", () -> new RouterBlock(DyeColor.YELLOW));
    public static final RegistryObject<RouterBlock> LIME_ROUTER = REGISTER.register("lime_router", () -> new RouterBlock(DyeColor.LIME));
    public static final RegistryObject<RouterBlock> PINK_ROUTER = REGISTER.register("pink_router", () -> new RouterBlock(DyeColor.PINK));
    public static final RegistryObject<RouterBlock> GRAY_ROUTER = REGISTER.register("gray_router", () -> new RouterBlock(DyeColor.GRAY));
    public static final RegistryObject<RouterBlock> LIGHT_GRAY_ROUTER = REGISTER.register("light_gray_router", () -> new RouterBlock(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<RouterBlock> CYAN_ROUTER = REGISTER.register("cyan_router", () -> new RouterBlock(DyeColor.CYAN));
    public static final RegistryObject<RouterBlock> PURPLE_ROUTER = REGISTER.register("purple_router", () -> new RouterBlock(DyeColor.PURPLE));
    public static final RegistryObject<RouterBlock> BLUE_ROUTER = REGISTER.register("blue_router", () -> new RouterBlock(DyeColor.BLUE));
    public static final RegistryObject<RouterBlock> BROWN_ROUTER = REGISTER.register("brown_router", () -> new RouterBlock(DyeColor.BROWN));
    public static final RegistryObject<RouterBlock> GREEN_ROUTER = REGISTER.register("green_router", () -> new RouterBlock(DyeColor.GREEN));
    public static final RegistryObject<RouterBlock> RED_ROUTER = REGISTER.register("red_router", () -> new RouterBlock(DyeColor.RED));
    public static final RegistryObject<RouterBlock> BLACK_ROUTER = REGISTER.register("black_router", () -> new RouterBlock(DyeColor.BLACK));
    public static final RegistryObject<PaperBlock> PAPER = REGISTER.register("paper", PaperBlock::new);

    public static final RegistryObject<OfficeChairBlock> WHITE_OFFICE_CHAIR = REGISTER.register("white_office_chair", () -> new OfficeChairBlock(DyeColor.WHITE));
    public static final RegistryObject<OfficeChairBlock> ORANGE_OFFICE_CHAIR = REGISTER.register("orange_office_chair", () -> new OfficeChairBlock(DyeColor.ORANGE));
    public static final RegistryObject<OfficeChairBlock> MAGENTA_OFFICE_CHAIR = REGISTER.register("magenta_office_chair", () -> new OfficeChairBlock(DyeColor.MAGENTA));
    public static final RegistryObject<OfficeChairBlock> LIGHT_BLUE_OFFICE_CHAIR = REGISTER.register("light_blue_office_chair", () -> new OfficeChairBlock(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<OfficeChairBlock> YELLOW_OFFICE_CHAIR = REGISTER.register("yellow_office_chair", () -> new OfficeChairBlock(DyeColor.YELLOW));
    public static final RegistryObject<OfficeChairBlock> LIME_OFFICE_CHAIR = REGISTER.register("lime_office_chair", () -> new OfficeChairBlock(DyeColor.LIME));
    public static final RegistryObject<OfficeChairBlock> PINK_OFFICE_CHAIR = REGISTER.register("pink_office_chair", () -> new OfficeChairBlock(DyeColor.PINK));
    public static final RegistryObject<OfficeChairBlock> GRAY_OFFICE_CHAIR = REGISTER.register("gray_office_chair", () -> new OfficeChairBlock(DyeColor.GRAY));
    public static final RegistryObject<OfficeChairBlock> LIGHT_GRAY_OFFICE_CHAIR = REGISTER.register("light_gray_office_chair", () -> new OfficeChairBlock(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<OfficeChairBlock> CYAN_OFFICE_CHAIR = REGISTER.register("cyan_office_chair", () -> new OfficeChairBlock(DyeColor.CYAN));
    public static final RegistryObject<OfficeChairBlock> PURPLE_OFFICE_CHAIR = REGISTER.register("purple_office_chair", () -> new OfficeChairBlock(DyeColor.PURPLE));
    public static final RegistryObject<OfficeChairBlock> BLUE_OFFICE_CHAIR = REGISTER.register("blue_office_chair", () -> new OfficeChairBlock(DyeColor.BLUE));
    public static final RegistryObject<OfficeChairBlock> BROWN_OFFICE_CHAIR = REGISTER.register("brown_office_chair", () -> new OfficeChairBlock(DyeColor.BROWN));
    public static final RegistryObject<OfficeChairBlock> GREEN_OFFICE_CHAIR = REGISTER.register("green_office_chair", () -> new OfficeChairBlock(DyeColor.GREEN));
    public static final RegistryObject<OfficeChairBlock> RED_OFFICE_CHAIR = REGISTER.register("red_office_chair", () -> new OfficeChairBlock(DyeColor.RED));
    public static final RegistryObject<OfficeChairBlock> BLACK_OFFICE_CHAIR = REGISTER.register("black_office_chair", () -> new OfficeChairBlock(DyeColor.BLACK));

    public static Stream<Block> getAllBlocks() {
        return REGISTER.getEntries().stream().map(RegistryObject::get);
    }

    public static List<LaptopBlock> getAllLaptops() {
        return getAllBlocks().filter(block -> block instanceof LaptopBlock).map(block -> (LaptopBlock) block).collect(Collectors.toList());
    }

    public static List<PrinterBlock> getAllPrinters() {
        return getAllBlocks().filter(block -> block instanceof PrinterBlock).map(block -> (PrinterBlock) block).collect(Collectors.toList());
    }

    public static List<RouterBlock> getAllRouters() {
        return getAllBlocks().filter(block -> block instanceof RouterBlock).map(block -> (RouterBlock) block).collect(Collectors.toList());
    }

    public static List<OfficeChairBlock> getAllOfficeChairs() {
        return getAllBlocks().filter(block -> block instanceof OfficeChairBlock).map(block -> (OfficeChairBlock) block).collect(Collectors.toList());
    }
}
