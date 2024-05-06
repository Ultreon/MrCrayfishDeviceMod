package dev.ultreon.devices.api.video;

import dev.ultreon.devices.mineos.apps.system.DisplayResolution;

public record CustomResolution(int width, int height) implements DisplayResolution {
}
