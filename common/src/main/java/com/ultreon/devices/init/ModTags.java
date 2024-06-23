package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import net.minecraft.util.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.item.Item;
import net.minecraft.block.Block;

/**
 * @author Qboi
 */
public final class ModTags {
    public static final class Items {
        public static final TagKey<Item> LAPTOPS = createTag("laptops");
        public static final TagKey<Item> PRINTERS = createTag("printers");
        public static final TagKey<Item> FLASH_DRIVES = createTag("flash_drives");
        public static final TagKey<Item> ROUTERS = createTag("routers");

        private static TagKey<Item> createTag(String name) {
            return TagKey.create(Registry.ITEM_REGISTRY, Devices.id(name));
        }
    }

    public static final class Blocks {
        public static final TagKey<Block> LAPTOPS = createTag("laptops");
        public static final TagKey<Block> PRINTERS = createTag("printers");
        public static final TagKey<Block> ROUTERS = createTag("routers");

        private static TagKey<Block> createTag(String name) {
            return TagKey.create(Registry.BLOCK_REGISTRY, Devices.id(name));
        }
    }
}
