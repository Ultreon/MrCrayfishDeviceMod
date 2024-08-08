package dev.ultreon.mineos;

import dev.ultreon.devices.api.bios.Bios;
import dev.ultreon.devices.api.bios.VEFI_Executable;
import dev.ultreon.devices.api.bios.efi.VEFI_System;

public class BootLoader implements VEFI_Executable {
    private Object kernel;

    @Override
    public void execute(Bios bios, VEFI_System system) {
        kernel = system.runIsolated(BootLoader.class, "dev.ultreon.mineos.kernel.MineOSKernel", "dev.ultreon.devices.os.mineos");
    }

    public Object getKernel() {
        return kernel;
    }
}
