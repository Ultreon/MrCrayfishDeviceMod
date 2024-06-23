package com.ultreon.devices.programs.gitweb.component.container.fabric;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeType;
import net.minecraft.tileentity.AbstractFurnaceBlockEntity;

public class FurnaceBoxImpl {
    public static int getBurnTime(ItemStack stack, RecipeType<?> type) {
        var a = AbstractFurnaceBlockEntity.getFuel().get(stack.getItem());
        return a == null ? 1600 : a;
    }
}
