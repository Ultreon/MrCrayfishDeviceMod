package dev.ultreon.devices.api.bios;

import dev.ultreon.devices.api.bios.efi.VEFI_System;

public interface Bios {
    void registerInterrupt(BiosInterruptType interrupt, InterruptHandler handler);

    Object call(BiosCallType call, Object[] args);

    void enableInterrupts();

    void disableInterrupts();

    VEFI_System getVEFISystem();
}
