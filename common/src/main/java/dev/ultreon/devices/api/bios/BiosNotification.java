package dev.ultreon.devices.api.bios;

import dev.ultreon.devices.api.app.IIcon;

public record BiosNotification(
        IIcon icon,
        String title,
        String subTitle
) {
}
