package dev.ultreon.devices.api.bios;

import dev.ultreon.devices.api.bios.efi.VEFI_System;

public interface VEFI_Executable {
    void execute(Bios bios, VEFI_System system);
}
