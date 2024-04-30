package com.ultreon.devices;

import com.ultreon.devices.api.os.OperatingSystem;
import com.ultreon.devices.core.BootLoader;
import com.ultreon.devices.mineos.client.MineOS;
import dev.architectury.registry.registries.RegistrySupplier;

import java.util.function.Supplier;

public class OperatingSystems {
    public static final RegistrySupplier<BootLoader<MineOS>> MINE_OS = register("mine_os", () -> MineOS::new);

    private static <T extends OperatingSystem> RegistrySupplier<BootLoader<T>> register(String name, Supplier<BootLoader<T>> supplier) {
        return UltreonDevicesMod.OPERATING_SYSTEM.register(UltreonDevicesMod.res(name), supplier);
    }

    public static void init() {

    }
}
