package com.ultreon.devices.util;

import net.minecraft.entity.player.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryUtil {
    public static int getItemAmount(PlayerEntity player, Item item) {
        int amount = 0;
        Inventory inventory = player.inventory;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                amount += stack.getCount();
            }
        }
        return amount;
    }

    public static boolean hasItemAndAmount(PlayerEntity player, Item item, int amount) {
        int count = 0;
        for (ItemStack stack : player.inventory.items) {
            if (stack != null && stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return amount <= count;
    }

    public static boolean removeItemWithAmount(PlayerEntity player, Item item, int amount) {
        if (hasItemAndAmount(player, item, amount)) {
            for (int i = 0; i < player.inventory.getContainerSize(); i++) {
                ItemStack stack = player.inventory.getItem(i);
                if (!stack.isEmpty() && stack.getItem() == item) {
                    if (amount - stack.getCount() < 0) {
                        stack.shrink(amount);
                        return true;
                    } else {
                        amount -= stack.getCount();
                        player.inventory.items.set(i, ItemStack.EMPTY);
                        if (amount == 0) return true;
                    }
                }
            }
        }
        return false;
    }
}
