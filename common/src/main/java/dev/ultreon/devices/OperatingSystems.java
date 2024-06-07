package dev.ultreon.devices;

import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.devices.api.os.OperatingSystem;
import dev.ultreon.devices.core.BootLoader;
import dev.ultreon.devices.mineos.client.MineOS;

import java.util.function.Supplier;

public class OperatingSystems {
    public static final RegistrySupplier<BootLoader<MineOS>> MINE_OS = register("mine_os", () -> MineOS::new);

    private static <T extends OperatingSystem> RegistrySupplier<BootLoader<T>> register(String name, Supplier<BootLoader<T>> supplier) {
        return UltreonDevicesMod.OPERATING_SYSTEM.register(UltreonDevicesMod.res(name), supplier);
    }

    public static void init() {

    }
}
