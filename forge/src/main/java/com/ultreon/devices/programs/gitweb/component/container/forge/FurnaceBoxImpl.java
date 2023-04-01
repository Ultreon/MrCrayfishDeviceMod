package com.ultreon.devices.programs.gitweb.component.container.forge;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class FurnaceBoxImpl {
    public static int getBurnTime(ItemStack stack, RecipeType<?> type) {
        return stack.getBurnTime(type);
    }
}
