package com.ultreon.devices.programs.debug;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.ButtonToggle;
import com.ultreon.devices.api.app.component.TextArea;
import com.ultreon.devices.api.app.interfaces.IHighlight;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

/**
 * @author MrCrayfish
 */
public class TextAreaApp extends Application {
    public static final IHighlight JAVA_HIGHLIGHT = text -> {
        if (text.startsWith("@")) return asArray(TextFormatting.YELLOW);

        if (text.startsWith("\"") && text.endsWith("\"")) return asArray(TextFormatting.AQUA);

        return switch (text) {
            case "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package", "synchronized",
                    "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw",
                    "byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient",
                    "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", "void", "class",
                    "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while", "null",
                    "record", "sealed" -> asArray(TextFormatting.BLUE);
            default -> asArray(TextFormatting.WHITE);
        };
    };

    @SafeVarargs
    private static <T> T[] asArray(T... t) {
        return t;
    }

    @Override
    public void init(@Nullable CompoundNBT intent) {
        Layout layout = new Layout(250, 150);

        TextArea textArea = new TextArea(5, 25, 240, 120);
        textArea.setScrollBarSize(5);
        layout.addComponent(textArea);

        ButtonToggle buttonWordWrap = new ButtonToggle(5, 5, Icons.ALIGN_JUSTIFY);
        buttonWordWrap.setToolTip("Word Wrap", "Break the lines to fit in the view");
        buttonWordWrap.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                textArea.setWrapText(!buttonWordWrap.isSelected());
            }
        });
        layout.addComponent(buttonWordWrap);

        ButtonToggle buttonDebug = new ButtonToggle(24, 5, Icons.HELP);
        buttonDebug.setToolTip("Debug Mode", "Show invisible characters");
        buttonDebug.setClickListener((mouseX, mouseY, mouseButton) -> {
//            if (mouseButton == 0) {
//                Laptop.font.setDebug(!buttonDebug.isSelected());
//            }
        });
        layout.addComponent(buttonDebug);

        ButtonToggle buttonHighlight = new ButtonToggle(43, 5, Icons.FONT);
        buttonHighlight.setToolTip("Highlight", "Set text highlighting to Java");
        buttonHighlight.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                if (!buttonHighlight.isSelected()) {
                    textArea.setHighlight(JAVA_HIGHLIGHT);
                } else {
                    textArea.setHighlight(null);
                }
            }
        });
        layout.addComponent(buttonHighlight);

        setCurrentLayout(layout);
    }

    @Override
    public void load(CompoundNBT tagCompound) {

    }

    @Override
    public void save(CompoundNBT tagCompound) {

    }
}
