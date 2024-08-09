package dev.ultreon.mineos;

import dev.ultreon.devices.api.bios.*;
import dev.ultreon.devices.api.bios.efi.VEFI_System;

public class BootLoader implements VEFI_Executable, InterruptHandler {
    private Object kernel = null;
    private Texture texture = null;

    @Override
    public void execute(Bios bios, VEFI_System system) {
        kernel = system.runIsolated(BootLoader.class, "dev.ultreon.mineos.kernel.MineOSKernel", "dev.ultreon.devices.os.mineos");

        system.offload(() -> {
            this.texture = new Texture(BootLoader.class.getResourceAsStream("/mineos.png"));
            this.texture.load(system);
        });

        system.getBios().registerInterrupt(BiosInterruptType.FRAMEBUFFER_INTERRUPT, this);
    }

    public Object getKernel() {
        return kernel;
    }

    @Override
    public void onInterrupt(InterruptData interrupt) {
        if (interrupt.interruptType() == BiosInterruptType.FRAMEBUFFER_INTERRUPT) {
            texture.render();
        }
    }
}
