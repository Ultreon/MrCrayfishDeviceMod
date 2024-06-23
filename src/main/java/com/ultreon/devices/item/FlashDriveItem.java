package com.ultreon.devices.item;

import com.ultreon.devices.Devices;
import com.ultreon.devices.IDeviceType;
import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.Reference;
import com.ultreon.devices.util.Colored;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FlashDriveItem extends Item implements Colored, SubItems, IDeviceType {

    private final DyeColor color;

    public FlashDriveItem(DyeColor color) {
        super(new Properties().tab(Devices.GROUP_DEVICE).rarity(Rarity.UNCOMMON).stacksTo(1));
        this.color = color;
    }

    private static TextFormatting getFromColor(DyeColor color) {
        switch (color) {
            case ORANGE:
            case BROWN:
                return TextFormatting.GOLD;
            case MAGENTA:
            case PINK:
                return TextFormatting.LIGHT_PURPLE;
            case LIGHT_BLUE:
                return TextFormatting.BLUE;
            case YELLOW:
                return TextFormatting.YELLOW;
            case LIME:
                return TextFormatting.GREEN;
            case GRAY:
                return TextFormatting.DARK_GRAY;
            case LIGHT_GRAY:
                return TextFormatting.GRAY;
            case CYAN:
                return TextFormatting.DARK_AQUA;
            case PURPLE:
                return TextFormatting.DARK_PURPLE;
            case BLUE:
                return TextFormatting.DARK_BLUE;
            case GREEN:
                return TextFormatting.DARK_GREEN;
            case RED:
                return TextFormatting.DARK_RED;
            case BLACK:
                return TextFormatting.BLACK;
            default:
                return TextFormatting.WHITE;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World level, List<net.minecraft.util.text.ITextComponent> tooltip, @NotNull ITooltipFlag isAdvanced) {
        String colorName = color.getName().replace("_", " ");
        colorName = WordUtils.capitalize(colorName);
        tooltip.add(new StringTextComponent("Color: " + TextFormatting.BOLD + getFromColor(color).toString() + colorName));
    }

    @Override
    public NonNullList<ResourceLocation> getModels() {
        NonNullList<ResourceLocation> modelLocations = NonNullList.create();
        for (DyeColor color : DyeColor.values())
            modelLocations.add(new ResourceLocation(Reference.MOD_ID, ForgeRegistries.ITEMS.getKey(this).getPath().substring(5) + "/" + color.getName()));
        return modelLocations;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public ModDeviceTypes getDeviceType() {
        return ModDeviceTypes.FLASH_DRIVE;
    }
}
