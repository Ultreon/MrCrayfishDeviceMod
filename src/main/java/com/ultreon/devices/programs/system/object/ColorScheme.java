package com.ultreon.devices.programs.system.object;

import net.minecraft.nbt.CompoundNBT;

import java.awt.*;

/**
 * @author MrCrayfish
 */
public class ColorScheme {
    public int textColor;
    public int textSecondaryColor;
    public int headerColor;
    public int backgroundColor;
    public int backgroundSecondaryColor;
    public int itemBackgroundColor;
    public int itemHighlightColor;

    public ColorScheme() {
        resetDefault();
    }

    public static ColorScheme fromTag(CompoundNBT tag) {
        ColorScheme scheme = new ColorScheme();
        if (tag.contains("textColor", Constants.NBT.TAG_INT)) {
            scheme.textColor = tag.getInt("textColor");
        }
        if (tag.contains("textSecondaryColor", Constants.NBT.TAG_INT)) {
            scheme.textSecondaryColor = tag.getInt("textSecondaryColor");
        }
        if (tag.contains("headerColor", Constants.NBT.TAG_INT)) {
            scheme.headerColor = tag.getInt("headerColor");
        }
        if (tag.contains("backgroundColor", Constants.NBT.TAG_INT)) {
            scheme.backgroundColor = tag.getInt("backgroundColor");
        }
        if (tag.contains("backgroundSecondaryColor", Constants.NBT.TAG_INT)) {
            scheme.backgroundSecondaryColor = tag.getInt("backgroundSecondaryColor");
        }
        if (tag.contains("itemBackgroundColor", Constants.NBT.TAG_INT)) {
            scheme.itemBackgroundColor = tag.getInt("itemBackgroundColor");
        }
        if (tag.contains("itemHighlightColor", Constants.NBT.TAG_INT)) {
            scheme.itemHighlightColor = tag.getInt("itemHighlightColor");
        }
        return scheme;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSecondaryColor() {
        return textSecondaryColor;
    }

    public void setTextSecondaryColor(int textSecondaryColor) {
        this.textSecondaryColor = textSecondaryColor;
    }

    public int getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(int headerColor) {
        this.headerColor = headerColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundSecondaryColor() {
        return backgroundSecondaryColor;
    }

    public void setBackgroundSecondaryColor(int backgroundSecondaryColor) {
        this.backgroundSecondaryColor = backgroundSecondaryColor;
    }

    public int getItemBackgroundColor() {
        return itemBackgroundColor;
    }

    public void setItemBackgroundColor(int itemBackgroundColor) {
        this.itemBackgroundColor = itemBackgroundColor;
    }

    public int getItemHighlightColor() {
        return itemHighlightColor;
    }

    public void setItemHighlightColor(int itemHighlightColor) {
        this.itemHighlightColor = itemHighlightColor;
    }

    public void resetDefault() {
        textColor = Color.decode("0xFFFFFF").getRGB();
        textSecondaryColor = Color.decode("0xABEFF4").getRGB();
        headerColor = Color.decode("0x387A96").getRGB();
        //backgroundColor = Color.decode("0x535861").getRGB();
        backgroundColor = Color.decode("0x6899C2").getRGB();
        backgroundSecondaryColor = Color.decode("0x36C052").getRGB();
        itemBackgroundColor = Color.decode("0x2E6897").getRGB();
        itemHighlightColor = Color.decode("0x8B74C9").getRGB();
    }

    public CompoundNBT toTag() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("textColor", textColor);
        tag.putInt("textSecondaryColor", textSecondaryColor);
        tag.putInt("headerColor", headerColor);
        tag.putInt("backgroundColor", backgroundColor);
        tag.putInt("backgroundSecondaryColor", backgroundSecondaryColor);
        tag.putInt("itemBackgroundColor", itemBackgroundColor);
        tag.putInt("itemHighlightColor", itemHighlightColor);
        return tag;
    }
}
