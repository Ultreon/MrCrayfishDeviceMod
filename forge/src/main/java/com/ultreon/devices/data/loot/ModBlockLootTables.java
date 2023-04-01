package com.ultreon.devices.data.loot;

import com.ultreon.devices.init.DeviceBlocks;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ModBlockLootTables extends VanillaBlockLoot {
    public ModBlockLootTables() {

    }

    @Override
    protected void generate() {
        DeviceBlocks.getAllLaptops().forEach(pBlock -> {
            if (pBlock.asItem() == Items.AIR) {
                throw new IllegalArgumentException("Block is not an item: " + RegistrarManager.getId(pBlock, Registries.BLOCK));
            }
            if (RegistrarManager.getId(pBlock, Registries.BLOCK).toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + RegistrarManager.getId(pBlock, Registries.BLOCK));
            }
            if (RegistrarManager.getId(pBlock, Registries.BLOCK).toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + RegistrarManager.getId(pBlock, Registries.BLOCK));
            }
            dropSelf(pBlock);
        });
        DeviceBlocks.getAllPrinters().forEach(pBlock -> {
            if (pBlock.asItem() == Items.AIR) {
                throw new IllegalArgumentException("Block is not an item: " + RegistrarManager.getId(pBlock, Registries.BLOCK));
            }
            if (RegistrarManager.getId(pBlock, Registries.BLOCK).toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + RegistrarManager.getId(pBlock, Registries.BLOCK));
            }
            if (RegistrarManager.getId(pBlock, Registries.BLOCK).toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + RegistrarManager.getId(pBlock, Registries.BLOCK));
            }
            dropSelf(pBlock);
        });
        DeviceBlocks.getAllRouters().forEach(pBlock -> {
            if (pBlock.asItem() == Items.AIR) {
                throw new IllegalArgumentException("Block is not an item: " + RegistrarManager.getId(pBlock, Registries.BLOCK));
            }
            if (RegistrarManager.getId(pBlock, Registries.BLOCK).toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + RegistrarManager.getId(pBlock, Registries.BLOCK));
            }
            if (RegistrarManager.getId(pBlock.asItem(), Registries.ITEM).toString().equals("minecraft:empty")) {
                throw new IllegalArgumentException("Block is not an item: " + RegistrarManager.getId(pBlock, Registries.BLOCK));
            }
            dropSelf(pBlock);
        });
    }

    @NotNull
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return DeviceBlocks.getAllBlocks().filter(pBlock -> pBlock.asItem() != Items.AIR && pBlock != DeviceBlocks.PAPER.get()).toList();
    }
}
