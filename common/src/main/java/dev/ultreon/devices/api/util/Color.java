package dev.ultreon.devices.api.util;

public class Color {
    public static final Color WHITE = new Color(java.awt.Color.WHITE);
    public static final Color BLACK = new Color(java.awt.Color.BLACK);
    public static final Color RED = new Color(java.awt.Color.RED);
    public static final Color GREEN = new Color(java.awt.Color.GREEN);
    public static final Color BLUE = new Color(java.awt.Color.BLUE);
    public static final Color YELLOW = new Color(java.awt.Color.YELLOW);
    public static final Color CYAN = new Color(java.awt.Color.CYAN);
    public static final Color MAGENTA = new Color(java.awt.Color.MAGENTA);
    public static final Color ORANGE = new Color(java.awt.Color.ORANGE);
    public static final Color PINK = new Color(java.awt.Color.PINK);
    public static final Color GRAY = new Color(java.awt.Color.GRAY);
    public static final Color DARK_GRAY = new Color(java.awt.Color.DARK_GRAY);
    public static final Color LIGHT_GRAY = new Color(java.awt.Color.LIGHT_GRAY);
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

    private final java.awt.Color awtColor;

    public Color() {
        this.awtColor = new java.awt.Color(0, 0, 0, 0);
    }

    public Color(int color) {
        this.awtColor = new java.awt.Color(color);
    }

    public Color(int rgba, boolean alpha) {
        this.awtColor = new java.awt.Color(rgba, alpha);
    }

    public Color(float r, float g, float b, float a) {
        this.awtColor = new java.awt.Color(r, g, b, a);
    }

    public Color(float r, float g, float b) {
        this.awtColor = new java.awt.Color(r, g, b, 1f);
    }

    public Color(int r, int g, int b, int a) {
        this.awtColor = new java.awt.Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    public Color(int r, int g, int b) {
        this.awtColor = new java.awt.Color(r / 255f, g / 255f, b / 255f, 1f);
    }

    public Color(java.awt.Color awtColor) {
        this.awtColor = awtColor;
    }

    public static float[] RGBtoHSB(int red, int green, int blue, float[] hsbVals) {
        return java.awt.Color.RGBtoHSB(red, green, blue, hsbVals);
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        return java.awt.Color.HSBtoRGB(hue, saturation, brightness);
    }

    public java.awt.Color getAwtColor() {
        return awtColor;
    }

    public int getRGB() {
        return awtColor.getRGB();
    }

    public int getRed() {
        return awtColor.getRed();
    }

    public int getGreen() {
        return awtColor.getGreen();
    }

    public int getBlue() {
        return awtColor.getBlue();
    }

    public int getAlpha() {
        return awtColor.getAlpha();
    }

    public float getRedF() {
        return awtColor.getRed() / 255f;
    }

    public float getGreenF() {
        return awtColor.getGreen() / 255f;
    }

    public float getBlueF() {
        return awtColor.getBlue() / 255f;
    }

    public float getAlphaF() {
        return awtColor.getAlpha() / 255f;
    }

    public Color brighter() {
        return new Color(this.awtColor.brighter());
    }
}
