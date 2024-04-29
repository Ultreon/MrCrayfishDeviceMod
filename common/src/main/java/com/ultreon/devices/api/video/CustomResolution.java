package com.ultreon.devices.api.video;

import com.ultreon.devices.programs.system.DisplayResolution;

public record CustomResolution(int width, int height) implements DisplayResolution {
}
