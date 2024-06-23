package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.registry.Registry;

/**
 * @author Qboi
 */
public final class ModTags {
    public static final class Items {
        public static final ITag.INamedTag<Item> LAPTOPS = createTag("laptops");
        public static final ITag.INamedTag<Item> PRINTERS = createTag("printers");
        public static final ITag.INamedTag<Item> FLASH_DRIVES = createTag("flash_drives");
        public static final ITag.INamedTag<Item> ROUTERS = createTag("routers");

        private static ITag.INamedTag<Item> createTag(String name) {
            return ItemTags.bind(Devices.MOD_ID + ":" + name);
        }
    }

    public static final class Blocks {
        public static final ITag.INamedTag<Block> LAPTOPS = createTag("laptops");
        public static final ITag.INamedTag<Block> PRINTERS = createTag("printers");
        public static final ITag.INamedTag<Block> ROUTERS = createTag("routers");

        private static ITag.INamedTag<Block> createTag(String name) {
            return BlockTags.bind(Devices.MOD_ID + ":" + name);
        }
    }
}
