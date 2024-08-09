package dev.ultreon.vbios.efi;

import dev.ultreon.vbios.Bios;

public interface VEFI_Executable {
    void execute(Bios bios, VEFI_System system);
}
