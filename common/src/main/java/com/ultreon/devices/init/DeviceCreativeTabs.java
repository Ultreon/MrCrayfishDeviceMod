package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.item.ColoredDeviceItem;
import com.ultreon.devices.item.FlashDriveItem;
import dev.ultreon.mods.xinexlib.platform.Services;
import dev.ultreon.mods.xinexlib.platform.services.IRegistrar;
import dev.ultreon.mods.xinexlib.platform.services.IRegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DeviceCreativeTabs {
    private static final IRegistrar<CreativeModeTab> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.CREATIVE_MODE_TAB);

    public static void register() {
    }

    public static final IRegistrySupplier<CreativeModeTab, CreativeModeTab> TAB = REGISTER.register("devices:devices_tab_device", () -> Services.creativeTabBuilder()
            .title(Component.translatable("itemGroup.devices.devices_tab_device"))
            .icon(() -> new ItemStack(DeviceBlocks.LAPTOPS.of(DyeColor.RED).get()))
            .displayItems((flags, output) -> {
                for (IRegistrySupplier<ColoredDeviceItem, Item> laptop : DeviceItems.LAPTOPS) {
                    output.accept(laptop.get());
                }
                output.accept(DeviceItems.MAC_MAX_X.get());
                for (IRegistrySupplier<ColoredDeviceItem, Item> printer : DeviceItems.PRINTERS) {
                    output.accept(printer.get());
                }
                for (IRegistrySupplier<ColoredDeviceItem, Item> router : DeviceItems.ROUTERS) {
                    output.accept(router.get());
                }
                for (IRegistrySupplier<ColoredDeviceItem, Item> office_chair : DeviceItems.OFFICE_CHAIRS) {
                    output.accept(office_chair.get());
                }
                for (IRegistrySupplier<FlashDriveItem, Item> flashdrive : DeviceItems.FLASH_DRIVE) {
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
            })
            .build());

    static {

    }
}
