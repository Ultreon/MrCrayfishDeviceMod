package dev.ultreon.devices.impl.bios;

public interface VideoMode {
    int getHorizontalResolution();
    int getVerticalResolution();
    int getBitsPerPixel();
    int getRefreshRate();
}
