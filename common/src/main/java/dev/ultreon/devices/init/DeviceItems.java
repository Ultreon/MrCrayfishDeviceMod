package dev.ultreon.devices.init;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.UltreonDevicesMod;
import dev.ultreon.devices.item.*;
import dev.ultreon.devices.util.DyeableRegistration;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class DeviceItems {
    private static final Registrar<Item> REGISTER = UltreonDevicesMod.REGISTRIES.get().get(Registries.ITEM);

    // Laptops
    public static final DyeableRegistration<Item> LAPTOPS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(UltreonDevicesMod.id(color.getName() + "_laptop"), () -> new ColoredDeviceItem(DeviceBlocks.LAPTOPS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.COMPUTER));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    // Custom Computers
    public static final RegistrySupplier<BlockItem> MAC_MAX_X = REGISTER.register(UltreonDevicesMod.id("mac_max_x"), () -> new DeviceItem(DeviceBlocks.MAC_MAX_X.get(), new Item.Properties(), ModDeviceTypes.COMPUTER) {
        @NotNull
        @Override
        public Component getDescription() {
            MutableComponent normalName = Component.translatable("block.devices.mac_max_x");
            if (Platform.isModLoaded("emojiful")) {
                return Component.translatable("block.devices.mac_max_x_emoji");
            }
            return normalName;
        }

        @NotNull
        @Override
        public Component getName(@NotNull ItemStack stack) {
            return getDescription();
        }
    });

    // Printers
    public static final DyeableRegistration<Item> PRINTERS = new DyeableRegistration<>() {
        @Override
        public RegistrySupplier<Item> register(Registrar<Item> registrar, DyeColor color) {
            return registrar.register(UltreonDevicesMod.id(color.getName() + "_printer"), () -> new ColoredDeviceItem(DeviceBlocks.PRINTERS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.PRINTER));
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
            return registrar.register(UltreonDevicesMod.id(color.getName() + "_router"), () -> new ColoredDeviceItem(DeviceBlocks.ROUTERS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.ROUTER));
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
            return registrar.register(UltreonDevicesMod.id(color.getName() + "_office_chair"), () -> new ColoredDeviceItem(DeviceBlocks.OFFICE_CHAIRS.of(color).get(), new Item.Properties(), color, ModDeviceTypes.SEAT));
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
            return registrar.register(UltreonDevicesMod.id(color.getName() + "_flash_drive"), () -> new FlashDriveItem(color));
        }

        @Override
        protected Registrar<Item> autoInit() {
            return REGISTER;
        }
    };

    public static final RegistrySupplier<BlockItem> PAPER = REGISTER.register(UltreonDevicesMod.id("paper"), () -> new BlockItem(DeviceBlocks.PAPER.get(), new Item.Properties()));

    public static final RegistrySupplier<BasicItem> PLASTIC_UNREFINED = REGISTER.register(UltreonDevicesMod.id("plastic_unrefined"), () -> new BasicItem(new Item.Properties()));
    public static final RegistrySupplier<BasicItem> PLASTIC = REGISTER.register(UltreonDevicesMod.id("plastic"), () -> new BasicItem(new Item.Properties()));
    public static final RegistrySupplier<BasicItem> PLASTIC_FRAME = REGISTER.register(UltreonDevicesMod.id("plastic_frame"), () -> new BasicItem(new Item.Properties()));
    public static final RegistrySupplier<BasicItem> WHEEL = REGISTER.register(UltreonDevicesMod.id("wheel"), () -> new BasicItem(new Item.Properties()));
    public static final RegistrySupplier<Item> GLASS_DUST = REGISTER.register(UltreonDevicesMod.id("glass_dust"), () -> new Item(new Item.Properties()));

    public static final RegistrySupplier<ComponentItem> COMPONENT_CIRCUIT_BOARD = REGISTER.register(UltreonDevicesMod.id("circuit_board"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_MOTHERBOARD = REGISTER.register(UltreonDevicesMod.id("motherboard"), () -> new MotherboardItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_MOTHERBOARD_FULL = REGISTER.register(UltreonDevicesMod.id("motherboard_full"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_CPU = REGISTER.register(UltreonDevicesMod.id("cpu"), () -> new MotherboardItem.Component(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_RAM = REGISTER.register(UltreonDevicesMod.id("ram"), () -> new MotherboardItem.Component(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_GPU = REGISTER.register(UltreonDevicesMod.id("gpu"), () -> new MotherboardItem.Component(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_WIFI = REGISTER.register(UltreonDevicesMod.id("wifi"), () -> new MotherboardItem.Component(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_HARD_DRIVE = REGISTER.register(UltreonDevicesMod.id("hard_drive"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_FLASH_CHIP = REGISTER.register(UltreonDevicesMod.id("flash_chip"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_SOLID_STATE_DRIVE = REGISTER.register(UltreonDevicesMod.id("solid_state_drive"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_BATTERY = REGISTER.register(UltreonDevicesMod.id("battery"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_SCREEN = REGISTER.register(UltreonDevicesMod.id("screen"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_CONTROLLER_UNIT = REGISTER.register(UltreonDevicesMod.id("controller_unit"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_SMALL_ELECTRIC_MOTOR = REGISTER.register(UltreonDevicesMod.id("small_electric_motor"), () -> new ComponentItem(new Item.Properties()));
    public static final RegistrySupplier<ComponentItem> COMPONENT_CARRIAGE = REGISTER.register(UltreonDevicesMod.id("carriage"), () -> new ComponentItem(new Item.Properties()));

    public static final RegistrySupplier<EthernetCableItem> ETHERNET_CABLE = REGISTER.register(UltreonDevicesMod.id("ethernet_cable"), EthernetCableItem::new);
    

    public static Stream<Item> getAllItems() {
        return REGISTER.getIds().stream().map(REGISTER::get);
    }

    @Nullable
    public static FlashDriveItem getFlashDriveByColor(DyeColor color) {
        return (FlashDriveItem) FLASH_DRIVE.of(color).get();
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
                .filter(item -> item.getDeviceType() == ModDeviceTypes.COMPUTER)
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
