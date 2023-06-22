package com.ultreon.devices.datagen;

import com.ultreon.devices.init.DeviceBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class DevicesRecipeProvider extends FabricRecipeProvider {
    public DevicesRecipeProvider(FabricDataOutput dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        DeviceBlocks.LAPTOPS.getMap().forEach(((dyeColor, blockRegistrySupplier) -> laptop(exporter, blockRegistrySupplier.get(), dyeColor)));
    }

    public static void laptop(Consumer<FinishedRecipe> finishedRecipeConsumer, ItemLike laptop, DyeColor color) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, laptop)
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
