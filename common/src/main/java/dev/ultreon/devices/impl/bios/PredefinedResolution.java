package dev.ultreon.devices.impl.bios;

public enum PredefinedResolution implements VideoMode {
    PREDEFINED_31360x17280(31360, 17280),
    PREDEFINED_15680x8640(15680, 8640),
    PREDEFINED_7840x4320(7840, 4320),
    PREDEFINED_3840x2160(3840, 2160),
    PREDEFINED_2560x1440(2560, 1440),
    PREDEFINED_1920x1080(1920, 1080),
    PREDEFINED_960x540(960, 540),
    PREDEFINED_800x450(800, 450),
    PREDEFINED_768x432(768, 432),
    PREDEFINED_696x360(696, 360),
    PREDEFINED_640x360(640, 360),
    PREDEFINED_512x288(512, 288),
    PREDEFINED_448x256(448, 256),
    PREDEFINED_384x216(384, 216);

    private final int width;
    private final int height;

    PredefinedResolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int getHorizontalResolution() {
        return width;
    }

    @Override
    public int getVerticalResolution() {
        return height;
    }

    @Override
    public int getBitsPerPixel() {
        return 32;
    }

    @Override
    public int getRefreshRate() {
        return 60;
    }

    public String toString() {
        return width + "x" + height;
    }
}
