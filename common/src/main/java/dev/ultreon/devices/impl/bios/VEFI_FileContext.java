package dev.ultreon.devices.impl.bios;

import dev.ultreon.devices.api.device.HardwareDevice;

public record VEFI_FileContext(HardwareDevice device, String fileInfo, int mode, int handle) {
    public void write(byte[] data, int offset, int length) {

    }
}
