package com.ultreon.devices.programs.system.object;

import com.ultreon.devices.Devices;

import java.util.function.Consumer;

public class ColorSchemes {
    public static final ColorScheme DEFAULT = createColorSceheme("default", new ColorScheme(), colorScheme -> {

    });

    public static final ColorScheme DARK = createColorSceheme("dark", new ColorScheme(), scheme -> {
        scheme.backgroundColor = 0x202020;
        scheme.textColor = 0xffffff;
        scheme.backgroundSecondaryColor = 0x303030;
        scheme.textSecondaryColor = 0xa0a0a0;
        scheme.itemBackgroundColor = 0x404040;
        scheme.itemHighlightColor = 0x505050;
        scheme.headerColor = 0x303030;
    });

    public static final ColorScheme LIGHT = createColorSceheme("light", new ColorScheme(), scheme -> {
        scheme.backgroundColor = 0xe0e0e0;
        scheme.textColor = 0x000000;
        scheme.backgroundSecondaryColor = 0xa0a0a0;
        scheme.textSecondaryColor = 0x303030;
        scheme.itemBackgroundColor = 0xb0b0b0;
        scheme.itemHighlightColor = 0xc0c0c0;
        scheme.headerColor = 0xa0a0a0;
    });

    private static ColorScheme createColorSceheme(String name, ColorScheme colorScheme, Consumer<ColorScheme> consumer) {
        consumer.accept(colorScheme);
        ColorSchemeRegistry.register(Devices.id(name), colorScheme);

        return colorScheme;
    }

    public static void init() {
        // No-op
    }
}
