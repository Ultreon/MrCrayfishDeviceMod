package dev.ultreon.devices.api.bios.efi;

public record VEFI_DiskInfo(VEFI_DeviceID deviceID, VEFI_DriveType driveType) {
    public VEFI_DiskInfo(VEFI_DeviceID deviceID, byte b) {
        this(deviceID, VEFI_DriveType.values()[b]);
    }
}
