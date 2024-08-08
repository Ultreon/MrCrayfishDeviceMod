package dev.ultreon.devices.api.boot;

import dev.ultreon.devices.api.bios.Bios;

public interface BootSector {
    void boot(Bios bios);
}
