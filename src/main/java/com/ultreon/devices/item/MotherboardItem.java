package com.ultreon.devices.item;

import com.ultreon.devices.util.KeyboardHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author MrCrayfish
 */
public class MotherboardItem extends ComponentItem {
    public MotherboardItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World level, List<net.minecraft.util.text.ITextComponent> tooltip, @NotNull ITooltipFlag isAdvanced) {
        CompoundNBT tag = stack.getTag();
        if (!KeyboardHelper.isShiftDown()) {
            tooltip.add(new StringTextComponent("CPU: " + getComponentStatus(tag, "cpu")));
            tooltip.add(new StringTextComponent("RAM: " + getComponentStatus(tag, "ram")));
            tooltip.add(new StringTextComponent("GPU: " + getComponentStatus(tag, "gpu")));
            tooltip.add(new StringTextComponent("WIFI: " + getComponentStatus(tag, "wifi")));
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + "Hold shift for help"));
        } else {
            tooltip.add(new StringTextComponent("To add the required components"));
            tooltip.add(new StringTextComponent("place the motherboard and the"));
            tooltip.add(new StringTextComponent("corresponding component into a"));
            tooltip.add(new StringTextComponent("crafting table to combine them."));
        }
    }

    private String getComponentStatus(CompoundNBT tag, String component) {
        if (tag != null && tag.contains("components", Constants.NBT.TAG_COMPOUND)) {
            CompoundNBT components = tag.getCompound("components");
            if (components.contains(component, Constants.NBT.TAG_BYTE)) {
                return TextFormatting.GREEN + "Added";
            }
        }
        return TextFormatting.RED + "Missing";
    }

    public static class Component extends ComponentItem {
        public Component(Properties properties) {
            super(properties);
        }
    }
}
