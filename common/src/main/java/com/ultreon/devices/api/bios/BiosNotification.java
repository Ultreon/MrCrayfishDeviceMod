package com.ultreon.devices.api.bios;

import com.ultreon.devices.api.app.IIcon;

public record BiosNotification(
        IIcon icon,
        String title,
        String subTitle
) {
}
