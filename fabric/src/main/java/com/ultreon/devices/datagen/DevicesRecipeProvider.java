package com.ultreon.devices.datagen;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.PrinterBlock;
import com.ultreon.devices.block.RouterBlock;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.init.DeviceItems;
import com.ultreon.devices.init.DeviceTags;
import com.ultreon.devices.init.ModTags;
import com.ultreon.devices.item.FlashDriveItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
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

        DeviceItems.getAllFlashDrives().forEach(flashDrive -> flashDrive(exporter, flashDrive, flashDrive.getColor()));
        DeviceBlocks.getAllPrinters().forEach(printer -> printer(exporter, printer));
        DeviceBlocks.getAllRouters().forEach(router -> router(exporter, router));

        // region <ComponentItems()>
        new ShapedRecipeBuilder(RecipeCategory.MISC, DeviceItems.COMPONENT_FLASH_CHIP.get(), 1)
                .pattern("iri")
                .pattern("ppp")
                .pattern("ggg")
                .define('p', DeviceItems.PLASTIC_FRAME.get())
                .define('i', Items.IRON_INGOT)
                .define('r', Items.REDSTONE)
                .define('g', Items.GOLD_NUGGET)
                .unlockedBy("has_laptop", has(ModTags.Items.LAPTOPS))
                .save(exporter);
        new ShapelessRecipeBuilder(RecipeCategory.MISC, DeviceItems.COMPONENT_MOTHERBOARD_FULL.get(), 1)
                .requires(DeviceItems.COMPONENT_CPU.get(), 1)
                .requires(DeviceItems.COMPONENT_GPU.get(), 1)
                .requires(DeviceItems.COMPONENT_RAM.get(), 1)
                .requires(DeviceItems.COMPONENT_WIFI.get(), 1)
                .requires(DeviceItems.COMPONENT_MOTHERBOARD.get(), 1)
                .unlockedBy("has_motherboard", has(DeviceItems.COMPONENT_MOTHERBOARD.get()))
                .save(exporter);
        // endregion
    }

    private static void router(Consumer<FinishedRecipe> exporter, RouterBlock router) {
        new ShapedRecipeBuilder(RecipeCategory.TOOLS, router, 1)
                .pattern("rdr")
                .pattern("ppp")
                .pattern("wcb")
                .define('d', DyeItem.byColor(router.getColor()))
                .define('r', Items.END_ROD)
                .define('p', DeviceItems.PLASTIC_FRAME.get())
                .define('w', DeviceItems.COMPONENT_WIFI.get())
                .define('c', DeviceItems.COMPONENT_CIRCUIT_BOARD.get())
                .define('b', DeviceItems.COMPONENT_BATTERY.get())
                .unlockedBy("has_circuit_board", has(DeviceItems.COMPONENT_CIRCUIT_BOARD.get()))
                .group(Devices.MOD_ID + ":router")
                .save(exporter);
    }

    private static void printer(Consumer<FinishedRecipe> exporter, PrinterBlock printer) {
        new ShapedRecipeBuilder(RecipeCategory.TOOLS, printer, 1)
                .pattern("psp")
                .pattern("mcb")
                .pattern("pdp")
                .define('d', DyeItem.byColor(printer.getColor()))
                .define('p', DeviceItems.PLASTIC_FRAME.get())
                .define('s', DeviceItems.COMPONENT_SCREEN.get())
                .define('m', DeviceItems.COMPONENT_SMALL_ELECTRIC_MOTOR.get())
                .define('c', DeviceItems.COMPONENT_CARRIAGE.get())
                .define('b', DeviceItems.COMPONENT_CONTROLLER_UNIT.get())
                .unlockedBy("has_carriage", has(DeviceItems.COMPONENT_CARRIAGE.get()))
                .group(Devices.MOD_ID + ":printer")
                .save(exporter);
    }

    private static void flashDrive(Consumer<FinishedRecipe> exporter, FlashDriveItem flashDrive, DyeColor color) {
        new ShapedRecipeBuilder(RecipeCategory.TOOLS, flashDrive, 1)
                .pattern("did")
                .pattern("pfp")
                .pattern("pcp")
                .define('d', DyeItem.byColor(color))
                .define('i', Items.IRON_INGOT)
                .define('p', DeviceItems.PLASTIC_FRAME.get())
                .define('f', DeviceItems.COMPONENT_FLASH_CHIP.get())
                .define('c', DeviceItems.COMPONENT_CIRCUIT_BOARD.get())
                .unlockedBy("has_flash_chip", has(DeviceItems.COMPONENT_FLASH_CHIP.get()))
                .group(Devices.MOD_ID + ":laptop")
                .save(exporter);
    }

    public static void laptop(Consumer<FinishedRecipe> exporter, ItemLike laptop, DyeColor color) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, laptop)
                .define('+', DyeUtils.getWoolFromDye(color))
                .define('/', Items.IRON_INGOT)
                .define('#', DeviceItems.COMPONENT_SCREEN.get())
                .define('.', Items.IRON_NUGGET)
                .define('$', DeviceItems.COMPONENT_BATTERY.get())
                .define('@', DeviceItems.COMPONENT_MOTHERBOARD_FULL.get())
                .define('O', DeviceTags.Items.INTERNAL_STORAGE)
                .pattern("+#+")
                .pattern(".@$")
                .pattern("/O/").group("devices:laptop")
                .unlockedBy(getHasName(Items.NETHERITE_INGOT), has(Items.NETHERITE_INGOT))
                .unlockedBy(getHasName(DeviceItems.COMPONENT_MOTHERBOARD_FULL.get()), has(DeviceItems.COMPONENT_MOTHERBOARD_FULL.get()))
                .save(exporter);
    }
}
