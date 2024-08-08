package dev.ultreon.mineos.kernel;

import dev.ultreon.devices.api.bios.efi.VEFI_DeviceID;
import dev.ultreon.devices.api.bios.efi.VEFI_DeviceInfo;
import dev.ultreon.devices.api.bios.efi.VEFI_System;

public interface Driver {
    void init(VEFI_System system, VEFI_DeviceID deviceID, VEFI_DeviceInfo deviceInfo);

    void load();

    void unload();

    String name();
}
