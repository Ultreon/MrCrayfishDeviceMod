package com.ultreon.devices.api.app.interfaces;

import net.minecraft.util.text.TextFormatting;

/**
 * @author MrCrayfish
 */
public interface IHighlight {
    TextFormatting[] getKeywordFormatting(String text);
}
