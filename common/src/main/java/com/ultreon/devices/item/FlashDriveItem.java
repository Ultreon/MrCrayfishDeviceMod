package com.ultreon.devices.item;

import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.Devices;
import com.ultreon.devices.IDeviceType;
import com.ultreon.devices.Reference;
import com.ultreon.devices.util.Colored;
import dev.architectury.registry.registries.Registries;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Registry;
import net.minecraft.util.text.Component;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.*;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class FlashDriveItem extends Item implements Colored, SubItems, IDeviceType {

    private final DyeColor color;

    public FlashDriveItem(DyeColor color) {
        super(new Properties().tab(Devices.GROUP_DEVICE).rarity(Rarity.UNCOMMON).stacksTo(1));
        this.color = color;
    }

    private static TextFormatting getFromColor(DyeColor color) {
        return switch (color) {
            case ORANGE, BROWN -> TextFormatting.GOLD;
            case MAGENTA, PINK -> TextFormatting.LIGHT_PURPLE;
            case LIGHT_BLUE -> TextFormatting.BLUE;
            case YELLOW -> TextFormatting.YELLOW;
            case LIME -> TextFormatting.GREEN;
            case GRAY -> TextFormatting.DARK_GRAY;
            case LIGHT_GRAY -> TextFormatting.GRAY;
            case CYAN -> TextFormatting.DARK_AQUA;
            case PURPLE -> TextFormatting.DARK_PURPLE;
            case BLUE -> TextFormatting.DARK_BLUE;
            case GREEN -> TextFormatting.DARK_GREEN;
            case RED -> TextFormatting.DARK_RED;
            case BLACK -> TextFormatting.BLACK;
            default -> TextFormatting.WHITE;
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(@NotNull ItemStack stack, @Nullable World level, List<Component> tooltip, @NotNull TooltipFlag isAdvanced) {
        String colorName = color.getName().replace("_", " ");
        colorName = WordUtils.capitalize(colorName);
        tooltip.add(new StringTextComponent("Color: " + TextFormatting.BOLD + getFromColor(color).toString() + colorName));
    }

    @Override
    public NonNullList<ResourceLocation> getModels() {
        NonNullList<ResourceLocation> modelLocations = NonNullList.create();
        for (DyeColor color : DyeColor.values())
            modelLocations.add(new ResourceLocation(Reference.MOD_ID, Objects.requireNonNull(Registries.getId(this, Registry.ITEM_REGISTRY)).getPath().substring(5) + "/" + color.getName()));
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
