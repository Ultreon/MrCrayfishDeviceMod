package com.ultreon.devices.datagen;

import com.ultreon.devices.init.DeviceBlocks;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class DevicesRecipeProvider extends FabricRecipeProvider {
    public DevicesRecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateRecipes(Consumer<FinishedRecipe> exporter) {
        DeviceBlocks.LAPTOPS.getMap().forEach(((dyeColor, blockRegistrySupplier) -> {
            laptop(exporter, blockRegistrySupplier.get(), dyeColor);
        }));
    }

    public static void laptop(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike laptop, DyeColor color) {
        ShapedRecipeBuilder.shaped(laptop)
                .define('+', DyeUtils.getWoolFromDye(color))
                .define('#', Items.NETHERITE_INGOT)
                .define('|', Items.QUARTZ)
                .define('_', Items.BEACON)
                .define('$', Items.GLASS)
                .pattern("#+#")
                .pattern("$|$")
                .pattern("_|_").group("devices:laptop")
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .unlockedBy(getHasName(Items.QUARTZ), has(Items.QUARTZ))
                .unlockedBy(getHasName(Items.BEACON), has(Items.BEACON))
     //         .unlockedBy(getHasName(Items.GLASS), has(Items.GLASS))
                .save(finishedRecipeConsumer);
    }
}
