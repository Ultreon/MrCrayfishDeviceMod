package dev.ultreon.vbios.efi;

import java.util.UUID;

public record VEFI_DeviceID(UUID id, boolean isDrive) {

}
