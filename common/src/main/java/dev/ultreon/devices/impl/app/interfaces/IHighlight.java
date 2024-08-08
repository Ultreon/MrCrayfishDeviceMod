package dev.ultreon.devices.impl.app.interfaces;

import net.minecraft.ChatFormatting;

/**
 * @author MrCrayfish
 */
public interface IHighlight {
    ChatFormatting[] getKeywordFormatting(String text);
}
