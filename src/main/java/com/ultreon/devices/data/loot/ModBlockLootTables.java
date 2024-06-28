package com.ultreon.devices.data.loot;

import com.ultreon.devices.init.DeviceBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Items;

import java.util.stream.Collectors;

public class ModBlockLootTables extends BlockLootTables {
    public ModBlockLootTables() {

    }

    @Override
    protected void addTables() {
        DeviceBlocks.getAllLaptops().forEach(pBlock -> {
            if (pBlock.asItem() == Items.AIR) {
                throw new IllegalArgumentException("Block is not an item: " + pBlock.getRegistryName());
            }
            if (pBlock.getRegistryName().toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + pBlock.getRegistryName());
            }
            if (pBlock.asItem().getRegistryName().toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + pBlock.getRegistryName());
            }
            dropSelf(pBlock);
        });
        DeviceBlocks.getAllPrinters().forEach(pBlock -> {
            if (pBlock.asItem() == Items.AIR) {
                throw new IllegalArgumentException("Block is not an item: " + pBlock.getRegistryName());
            }
            if (pBlock.getRegistryName().toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + pBlock.getRegistryName());
            }
            if (pBlock.asItem().getRegistryName().toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + pBlock.getRegistryName());
            }
            dropSelf(pBlock);
        });
        DeviceBlocks.getAllRouters().forEach(pBlock -> {
            if (pBlock.asItem() == Items.AIR) {
                throw new IllegalArgumentException("Block is not an item: " + pBlock.getRegistryName());
            }
            if (pBlock.getRegistryName().toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + pBlock.getRegistryName());
            }
            if (pBlock.asItem().getRegistryName().toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + pBlock.getRegistryName());
            }
            dropSelf(pBlock);
        });
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return DeviceBlocks.getAllBlocks().filter(pBlock -> pBlock.asItem() != Items.AIR && pBlock != DeviceBlocks.PAPER.get()).collect(Collectors.toList());
    }
}
