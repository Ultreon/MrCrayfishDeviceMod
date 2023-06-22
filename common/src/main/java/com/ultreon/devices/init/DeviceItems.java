package com.ultreon.devices.init;

import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.Devices;
import com.ultreon.devices.item.*;
import com.ultreon.devices.util.DyeableRegistration;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class DeviceItems {
    private static final Registrar<Item> REGISTER = Devices.REGISTRIES.get().get(Registry.ITEM_REGISTRY);

    // Laptops
    public static final DyeableRegistration<Item> LAPTOPS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_laptop"), () -> new ColoredDeviceItem(DeviceBlocks.LAPTOPS.of(color).get(), new Item.Properties().tab(Devices.TAB_DEVICE), color, ModDeviceTypes.LAPTOP));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Printers
    public static final DyeableRegistration<Item> PRINTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_printer"), () -> new ColoredDeviceItem(DeviceBlocks.PRINTERS.of(color).get(), new Item.Properties().tab(Devices.TAB_DEVICE), color, ModDeviceTypes.PRINTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Routers
    public static final DyeableRegistration<Item> ROUTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_router"), () -> new ColoredDeviceItem(DeviceBlocks.ROUTERS.of(color).get(), new Item.Properties().tab(Devices.TAB_DEVICE), color, ModDeviceTypes.ROUTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Office Chairs
    public static final DyeableRegistration<Item> OFFICE_CHAIRS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_office_chair"), () -> new ColoredDeviceItem(DeviceBlocks.OFFICE_CHAIRS.of(color).get(), new Item.Properties().tab(Devices.TAB_DEVICE), color, ModDeviceTypes.SEAT));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Flash drives
    public static final DyeableRegistration<Item> FLASH_DRIVE = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(Devices.id(color.getName() + "_flash_drive"), () -> new FlashDriveItem(DyeColor.WHITE));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    public static final RegistrySupplier<BlockItem> PAPER = REGISTER.register(Devices.id("paper"), () -> new BlockItem(DeviceBlocks.PAPER.get(), new Item.Properties().tab(Devices.TAB_DEVICE)));

    public static final RegistrySupplier<BasicItem> PLASTIC_UNREFINED = REGISTER.register(Devices.id("plastic_unrefined"), () -> new BasicItem(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<BasicItem> PLASTIC = REGISTER.register(Devices.id("plastic"), () -> new BasicItem(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<BasicItem> PLASTIC_FRAME = REGISTER.register(Devices.id("plastic_frame"), () -> new BasicItem(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<BasicItem> WHEEL = REGISTER.register(Devices.id("wheel"), () -> new BasicItem(new Item.Properties().tab(Devices.TAB_DEVICE)));

    public static final RegistrySupplier<ComponentItem> COMPONENT_CIRCUIT_BOARD = REGISTER.register(Devices.id("circuit_board"), () -> new ComponentItem(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_MOTHERBOARD = REGISTER.register(Devices.id("motherboard"), () -> new MotherboardItem(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_CPU = REGISTER.register(Devices.id("cpu"), () -> new MotherboardItem.Component(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_RAM = REGISTER.register(Devices.id("ram"), () -> new MotherboardItem.Component(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_GPU = REGISTER.register(Devices.id("gpu"), () -> new MotherboardItem.Component(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_WIFI = REGISTER.register(Devices.id("wifi"), () -> new MotherboardItem.Component(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_HARD_DRIVE = REGISTER.register(Devices.id("hard_drive"), () -> new ComponentItem(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_FLASH_CHIP = REGISTER.register(Devices.id("flash_chip"), () -> new ComponentItem(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_SOLID_STATE_DRIVE = REGISTER.register(Devices.id("solid_state_drive"), () -> new ComponentItem(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_BATTERY = REGISTER.register(Devices.id("battery"), () -> new ComponentItem(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_SCREEN = REGISTER.register(Devices.id("screen"), () -> new ComponentItem(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_CONTROLLER_UNIT = REGISTER.register(Devices.id("controller_unit"), () -> new ComponentItem(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_SMALL_ELECTRIC_MOTOR = REGISTER.register(Devices.id("small_electric_motor"), () -> new ComponentItem(new Item.Properties().tab(Devices.TAB_DEVICE)));
    public static final RegistrySupplier<ComponentItem> COMPONENT_CARRIAGE = REGISTER.register(Devices.id("carriage"), () -> new ComponentItem(new Item.Properties().tab(Devices.TAB_DEVICE)));

    public static final RegistrySupplier<EthernetCableItem> ETHERNET_CABLE = REGISTER.register(Devices.id("ethernet_cable"), () -> new EthernetCableItem());
    

    public static Stream<Item> getAllItems() {
        return REGISTER.getIds().stream().map(REGISTER::get);
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
                .toList();
    }

    public static List<ColoredDeviceItem> getAllLaptops() {
        return getAllItems()
                .filter(item -> item.asItem() instanceof ColoredDeviceItem)
                .map(item -> (ColoredDeviceItem) item.asItem())
                .filter(item -> item.getDeviceType() == ModDeviceTypes.LAPTOP)
                .toList();
    }

    public static List<ColoredDeviceItem> getAllPrinters() {
        return getAllItems()
                .filter(item -> item.asItem() instanceof ColoredDeviceItem)
                .map(item -> (ColoredDeviceItem) item.asItem())
                .filter(item -> item.getDeviceType() == ModDeviceTypes.PRINTER)
                .toList();
    }

    public static List<ColoredDeviceItem> getAllRouters() {
        return getAllItems()
                .filter(item -> item.asItem() instanceof ColoredDeviceItem)
                .map(item -> (ColoredDeviceItem) item.asItem())
                .filter(item -> item.getDeviceType() == ModDeviceTypes.ROUTER)
                .toList();
    }

    public static void register() {

    }
}
