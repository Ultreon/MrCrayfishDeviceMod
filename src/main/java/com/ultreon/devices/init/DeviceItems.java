package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.item.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class DeviceItems {
    private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Devices.MOD_ID);

    // Laptops
    public static final RegistryObject<BlockItem> WHITE_LAPTOP = REGISTER.register("white_laptop", () -> new ColoredDeviceItem(DeviceBlocks.WHITE_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.WHITE, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> ORANGE_LAPTOP = REGISTER.register("orange_laptop", () -> new ColoredDeviceItem(DeviceBlocks.ORANGE_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.ORANGE, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> MAGENTA_LAPTOP = REGISTER.register("magenta_laptop", () -> new ColoredDeviceItem(DeviceBlocks.MAGENTA_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.MAGENTA, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> LIGHT_BLUE_LAPTOP = REGISTER.register("light_blue_laptop", () -> new ColoredDeviceItem(DeviceBlocks.LIGHT_BLUE_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.LIGHT_BLUE, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> YELLOW_LAPTOP = REGISTER.register("yellow_laptop", () -> new ColoredDeviceItem(DeviceBlocks.YELLOW_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.YELLOW, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> LIME_LAPTOP = REGISTER.register("lime_laptop", () -> new ColoredDeviceItem(DeviceBlocks.LIME_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.LIME, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> PINK_LAPTOP = REGISTER.register("pink_laptop", () -> new ColoredDeviceItem(DeviceBlocks.PINK_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.PINK, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> GRAY_LAPTOP = REGISTER.register("gray_laptop", () -> new ColoredDeviceItem(DeviceBlocks.GRAY_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.GRAY, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> LIGHT_GRAY_LAPTOP = REGISTER.register("light_gray_laptop", () -> new ColoredDeviceItem(DeviceBlocks.LIGHT_GRAY_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.LIGHT_GRAY, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> CYAN_LAPTOP = REGISTER.register("cyan_laptop", () -> new ColoredDeviceItem(DeviceBlocks.CYAN_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.CYAN, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> PURPLE_LAPTOP = REGISTER.register("purple_laptop", () -> new ColoredDeviceItem(DeviceBlocks.PURPLE_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.PURPLE, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> BLUE_LAPTOP = REGISTER.register("blue_laptop", () -> new ColoredDeviceItem(DeviceBlocks.BLUE_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.BLUE, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> BROWN_LAPTOP = REGISTER.register("brown_laptop", () -> new ColoredDeviceItem(DeviceBlocks.BROWN_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.BROWN, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> GREEN_LAPTOP = REGISTER.register("green_laptop", () -> new ColoredDeviceItem(DeviceBlocks.GREEN_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.GREEN, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> RED_LAPTOP = REGISTER.register("red_laptop", () -> new ColoredDeviceItem(DeviceBlocks.RED_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.RED, ModDeviceTypes.LAPTOP));
    public static final RegistryObject<BlockItem> BLACK_LAPTOP = REGISTER.register("black_laptop", () -> new ColoredDeviceItem(DeviceBlocks.BLACK_LAPTOP.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.BLACK, ModDeviceTypes.LAPTOP));

    // Printers
    public static final RegistryObject<BlockItem> WHITE_PRINTER = REGISTER.register("white_printer", () -> new ColoredDeviceItem(DeviceBlocks.WHITE_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.WHITE, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> ORANGE_PRINTER = REGISTER.register("orange_printer", () -> new ColoredDeviceItem(DeviceBlocks.ORANGE_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.ORANGE, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> MAGENTA_PRINTER = REGISTER.register("magenta_printer", () -> new ColoredDeviceItem(DeviceBlocks.MAGENTA_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.MAGENTA, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> LIGHT_BLUE_PRINTER = REGISTER.register("light_blue_printer", () -> new ColoredDeviceItem(DeviceBlocks.LIGHT_BLUE_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.LIGHT_BLUE, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> YELLOW_PRINTER = REGISTER.register("yellow_printer", () -> new ColoredDeviceItem(DeviceBlocks.YELLOW_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.YELLOW, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> LIME_PRINTER = REGISTER.register("lime_printer", () -> new ColoredDeviceItem(DeviceBlocks.LIME_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.LIME, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> PINK_PRINTER = REGISTER.register("pink_printer", () -> new ColoredDeviceItem(DeviceBlocks.PINK_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.PINK, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> GRAY_PRINTER = REGISTER.register("gray_printer", () -> new ColoredDeviceItem(DeviceBlocks.GRAY_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.GRAY, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> LIGHT_GRAY_PRINTER = REGISTER.register("light_gray_printer", () -> new ColoredDeviceItem(DeviceBlocks.LIGHT_GRAY_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.LIGHT_GRAY, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> CYAN_PRINTER = REGISTER.register("cyan_printer", () -> new ColoredDeviceItem(DeviceBlocks.CYAN_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.CYAN, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> PURPLE_PRINTER = REGISTER.register("purple_printer", () -> new ColoredDeviceItem(DeviceBlocks.PURPLE_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.PURPLE, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> BLUE_PRINTER = REGISTER.register("blue_printer", () -> new ColoredDeviceItem(DeviceBlocks.BLUE_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.BLUE, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> BROWN_PRINTER = REGISTER.register("brown_printer", () -> new ColoredDeviceItem(DeviceBlocks.BROWN_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.BROWN, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> GREEN_PRINTER = REGISTER.register("green_printer", () -> new ColoredDeviceItem(DeviceBlocks.GREEN_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.GREEN, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> RED_PRINTER = REGISTER.register("red_printer", () -> new ColoredDeviceItem(DeviceBlocks.RED_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.RED, ModDeviceTypes.PRINTER));
    public static final RegistryObject<BlockItem> BLACK_PRINTER = REGISTER.register("black_printer", () -> new ColoredDeviceItem(DeviceBlocks.BLACK_PRINTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.BLACK, ModDeviceTypes.PRINTER));

    // Routers
    public static final RegistryObject<BlockItem> WHITE_ROUTER = REGISTER.register("white_router", () -> new ColoredDeviceItem(DeviceBlocks.WHITE_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.WHITE, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> ORANGE_ROUTER = REGISTER.register("orange_router", () -> new ColoredDeviceItem(DeviceBlocks.ORANGE_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.ORANGE, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> MAGENTA_ROUTER = REGISTER.register("magenta_router", () -> new ColoredDeviceItem(DeviceBlocks.MAGENTA_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.MAGENTA, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> LIGHT_BLUE_ROUTER = REGISTER.register("light_blue_router", () -> new ColoredDeviceItem(DeviceBlocks.LIGHT_BLUE_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.LIGHT_BLUE, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> YELLOW_ROUTER = REGISTER.register("yellow_router", () -> new ColoredDeviceItem(DeviceBlocks.YELLOW_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.YELLOW, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> LIME_ROUTER = REGISTER.register("lime_router", () -> new ColoredDeviceItem(DeviceBlocks.LIME_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.LIME, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> PINK_ROUTER = REGISTER.register("pink_router", () -> new ColoredDeviceItem(DeviceBlocks.PINK_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.PINK, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> GRAY_ROUTER = REGISTER.register("gray_router", () -> new ColoredDeviceItem(DeviceBlocks.GRAY_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.GRAY, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> LIGHT_GRAY_ROUTER = REGISTER.register("light_gray_router", () -> new ColoredDeviceItem(DeviceBlocks.LIGHT_GRAY_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.LIGHT_GRAY, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> CYAN_ROUTER = REGISTER.register("cyan_router", () -> new ColoredDeviceItem(DeviceBlocks.CYAN_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.CYAN, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> PURPLE_ROUTER = REGISTER.register("purple_router", () -> new ColoredDeviceItem(DeviceBlocks.PURPLE_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.PURPLE, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> BLUE_ROUTER = REGISTER.register("blue_router", () -> new ColoredDeviceItem(DeviceBlocks.BLUE_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.BLUE, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> BROWN_ROUTER = REGISTER.register("brown_router", () -> new ColoredDeviceItem(DeviceBlocks.BROWN_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.BROWN, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> GREEN_ROUTER = REGISTER.register("green_router", () -> new ColoredDeviceItem(DeviceBlocks.GREEN_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.GREEN, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> RED_ROUTER = REGISTER.register("red_router", () -> new ColoredDeviceItem(DeviceBlocks.RED_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.RED, ModDeviceTypes.ROUTER));
    public static final RegistryObject<BlockItem> BLACK_ROUTER = REGISTER.register("black_router", () -> new ColoredDeviceItem(DeviceBlocks.BLACK_ROUTER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE), DyeColor.BLACK, ModDeviceTypes.ROUTER));

    // Flash drives
    public static final RegistryObject<FlashDriveItem> WHITE_FLASH_DRIVE = REGISTER.register("white_flash_drive", () -> new FlashDriveItem(DyeColor.WHITE));
    public static final RegistryObject<FlashDriveItem> ORANGE_FLASH_DRIVE = REGISTER.register("orange_flash_drive", () -> new FlashDriveItem(DyeColor.ORANGE));
    public static final RegistryObject<FlashDriveItem> MAGENTA_FLASH_DRIVE = REGISTER.register("magenta_flash_drive", () -> new FlashDriveItem(DyeColor.MAGENTA));
    public static final RegistryObject<FlashDriveItem> LIGHT_BLUE_FLASH_DRIVE = REGISTER.register("light_blue_flash_drive", () -> new FlashDriveItem(DyeColor.LIGHT_BLUE));
    public static final RegistryObject<FlashDriveItem> YELLOW_FLASH_DRIVE = REGISTER.register("yellow_flash_drive", () -> new FlashDriveItem(DyeColor.YELLOW));
    public static final RegistryObject<FlashDriveItem> LIME_FLASH_DRIVE = REGISTER.register("lime_flash_drive", () -> new FlashDriveItem(DyeColor.LIME));
    public static final RegistryObject<FlashDriveItem> PINK_FLASH_DRIVE = REGISTER.register("pink_flash_drive", () -> new FlashDriveItem(DyeColor.PINK));
    public static final RegistryObject<FlashDriveItem> GRAY_FLASH_DRIVE = REGISTER.register("gray_flash_drive", () -> new FlashDriveItem(DyeColor.GRAY));
    public static final RegistryObject<FlashDriveItem> LIGHT_GRAY_FLASH_DRIVE = REGISTER.register("light_gray_flash_drive", () -> new FlashDriveItem(DyeColor.LIGHT_GRAY));
    public static final RegistryObject<FlashDriveItem> CYAN_FLASH_DRIVE = REGISTER.register("cyan_flash_drive", () -> new FlashDriveItem(DyeColor.CYAN));
    public static final RegistryObject<FlashDriveItem> PURPLE_FLASH_DRIVE = REGISTER.register("purple_flash_drive", () -> new FlashDriveItem(DyeColor.PURPLE));
    public static final RegistryObject<FlashDriveItem> BLUE_FLASH_DRIVE = REGISTER.register("blue_flash_drive", () -> new FlashDriveItem(DyeColor.BLUE));
    public static final RegistryObject<FlashDriveItem> BROWN_FLASH_DRIVE = REGISTER.register("brown_flash_drive", () -> new FlashDriveItem(DyeColor.BROWN));
    public static final RegistryObject<FlashDriveItem> GREEN_FLASH_DRIVE = REGISTER.register("green_flash_drive", () -> new FlashDriveItem(DyeColor.GREEN));
    public static final RegistryObject<FlashDriveItem> RED_FLASH_DRIVE = REGISTER.register("red_flash_drive", () -> new FlashDriveItem(DyeColor.RED));
    public static final RegistryObject<FlashDriveItem> BLACK_FLASH_DRIVE = REGISTER.register("black_flash_drive", () -> new FlashDriveItem(DyeColor.BLACK));
    public static final RegistryObject<BlockItem> PAPER = REGISTER.register("paper", () -> new BlockItem(DeviceBlocks.PAPER.get(), new Item.Properties().tab(Devices.GROUP_DEVICE)));

    public static final RegistryObject<BasicItem> PLASTIC_UNREFINED = REGISTER.register("plastic_unrefined", () -> new BasicItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<BasicItem> PLASTIC = REGISTER.register("plastic", () -> new BasicItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<BasicItem> PLASTIC_FRAME = REGISTER.register("plastic_frame", () -> new BasicItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<BasicItem> WHEEL = REGISTER.register("wheel", () -> new BasicItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));

    public static final RegistryObject<ComponentItem> COMPONENT_CIRCUIT_BOARD = REGISTER.register("circuit_board", () -> new ComponentItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_MOTHERBOARD = REGISTER.register("motherboard", () -> new MotherboardItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_CPU = REGISTER.register("cpu", () -> new MotherboardItem.Component(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_RAM = REGISTER.register("ram", () -> new MotherboardItem.Component(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_GPU = REGISTER.register("gpu", () -> new MotherboardItem.Component(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_WIFI = REGISTER.register("wifi", () -> new MotherboardItem.Component(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_HARD_DRIVE = REGISTER.register("hard_drive", () -> new ComponentItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_FLASH_CHIP = REGISTER.register("flash_chip", () -> new ComponentItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_SOLID_STATE_DRIVE = REGISTER.register("solid_state_drive", () -> new ComponentItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_BATTERY = REGISTER.register("battery", () -> new ComponentItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_SCREEN = REGISTER.register("screen", () -> new ComponentItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_CONTROLLER_UNIT = REGISTER.register("controller_unit", () -> new ComponentItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_SMALL_ELECTRIC_MOTOR = REGISTER.register("small_electric_motor", () -> new ComponentItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));
    public static final RegistryObject<ComponentItem> COMPONENT_CARRIAGE = REGISTER.register("carriage", () -> new ComponentItem(new Item.Properties().tab(Devices.GROUP_DEVICE)));

    public static final RegistryObject<EthernetCableItem> ETHERNET_CABLE = REGISTER.register("ethernet_cable", EthernetCableItem::new);
    

    public static Stream<Item> getAllItems() {
        return REGISTER.getEntries().stream().map(RegistryObject::get);
    }

    @Nullable
    public static FlashDriveItem getFlashDriveByColor(DyeColor color) {
        return getAllFlashDrives().stream()
                .filter(item -> item.getColor() == color)
                .findFirst()
                .orElse(null);
    }

    public static List<FlashDriveItem> getAllFlashDrives() {
        return getAllItems()
                .filter(item -> item.asItem() instanceof FlashDriveItem)
                .map(item -> (FlashDriveItem) item.asItem())
                .collect(Collectors.toList());
    }

    public static List<ColoredDeviceItem> getAllLaptops() {
        return getAllItems()
                .filter(item -> item.asItem() instanceof ColoredDeviceItem)
                .map(item -> (ColoredDeviceItem) item.asItem())
                .filter(item -> item.getDeviceType() == ModDeviceTypes.LAPTOP)
                .collect(Collectors.toList());
    }

    public static List<ColoredDeviceItem> getAllPrinters() {
        return getAllItems()
                .filter(item -> item.asItem() instanceof ColoredDeviceItem)
                .map(item -> (ColoredDeviceItem) item.asItem())
                .filter(item -> item.getDeviceType() == ModDeviceTypes.PRINTER)
                .collect(Collectors.toList());
    }

    public static List<ColoredDeviceItem> getAllRouters() {
        return getAllItems()
                .filter(item -> item.asItem() instanceof ColoredDeviceItem)
                .map(item -> (ColoredDeviceItem) item.asItem())
                .filter(item -> item.getDeviceType() == ModDeviceTypes.ROUTER)
                .collect(Collectors.toList());
    }


    public static void register() {
        REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
