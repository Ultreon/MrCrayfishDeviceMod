package dev.ultreon.devices.impl.bios;

import dev.ultreon.vbios.efi.VEFI_DeviceID;

import java.util.ArrayList;
import java.util.List;

public class DeviceInfo {
    public final List<VEFI_DeviceID> deviceList = new ArrayList<>();

    public VEFI_DeviceID[] createDeviceList() {
        return deviceList.toArray(new VEFI_DeviceID[0]);
    }

    public VEFI_DeviceID[] createDriveList() {
        return deviceList.stream().filter(VEFI_DeviceID::isDrive).toArray(VEFI_DeviceID[]::new);
    }
}
