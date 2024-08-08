package dev.ultreon.devices.impl.video;

import dev.ultreon.mineos.apps.system.DisplayResolution;

public record CustomResolution(int width, int height) implements DisplayResolution {
}
