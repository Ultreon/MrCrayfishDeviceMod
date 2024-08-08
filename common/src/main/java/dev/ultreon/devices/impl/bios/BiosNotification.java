package dev.ultreon.devices.impl.bios;

import dev.ultreon.devices.impl.app.IIcon;

public record BiosNotification(
        IIcon icon,
        String title,
        String subTitle
) {
}
