package com.ultreon.devices.programs.gitweb.component.container.forge;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;

public class FurnaceBoxImpl {
    public static int getBurnTime(ItemStack stack, IRecipeType<?> type) {
        return stack.getBurnTime(type);
    }
}
