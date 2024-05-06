package dev.ultreon.devices;

import dev.ultreon.devices.init.DeviceItems;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import static dev.ultreon.devices.UltreonDevicesMod.id;

public class DeviceTab {
    @SuppressWarnings("UnstableApiUsage")
    public static DeferredSupplier<CreativeModeTab> create() {
        UltreonDevicesMod.LOGGER.info("Creating Creative Tab...");
        DeferredSupplier<CreativeModeTab> devicesTabDevice = CreativeTabRegistry.defer(id("devices_tab_device")); //TODO () -> new ItemStack(DeviceBlocks.LAPTOPS.of(DyeColor.RED).get()
        CreativeTabRegistry.modify(devicesTabDevice, (flags, output, canUseGameMasterBlocks) -> {
            for (RegistrySupplier<Item> laptop : DeviceItems.LAPTOPS) {
                output.accept(laptop.get());
            }
            output.accept(DeviceItems.MAC_MAX_X.get());
            for (RegistrySupplier<Item> printer : DeviceItems.PRINTERS) {
                output.accept(printer.get());
            }
            for (RegistrySupplier<Item> router : DeviceItems.ROUTERS) {
                output.accept(router.get());
            }
            for (RegistrySupplier<Item> office_chair : DeviceItems.OFFICE_CHAIRS) {
                output.accept(office_chair.get());
            }
            for (RegistrySupplier<Item> flashdrive : DeviceItems.FLASH_DRIVE) {
                output.accept(flashdrive.get());
            }
            output.accept(DeviceItems.COMPONENT_CPU.get());
            output.accept(DeviceItems.COMPONENT_SOLID_STATE_DRIVE.get());
            output.accept(DeviceItems.COMPONENT_GPU.get());
            output.accept(DeviceItems.COMPONENT_RAM.get());
            output.accept(DeviceItems.COMPONENT_HARD_DRIVE.get());
            output.accept(DeviceItems.COMPONENT_BATTERY.get());
            output.accept(DeviceItems.COMPONENT_SCREEN.get());
            output.accept(DeviceItems.COMPONENT_WIFI.get());
            output.accept(DeviceItems.COMPONENT_CARRIAGE.get());
            output.accept(DeviceItems.COMPONENT_FLASH_CHIP.get());
            output.accept(DeviceItems.COMPONENT_CIRCUIT_BOARD.get());
            output.accept(DeviceItems.COMPONENT_CONTROLLER_UNIT.get());
            output.accept(DeviceItems.COMPONENT_SMALL_ELECTRIC_MOTOR.get());
            output.accept(DeviceItems.COMPONENT_MOTHERBOARD.get());
        });
        return devicesTabDevice;
    }
}
