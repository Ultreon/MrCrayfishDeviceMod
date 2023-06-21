package com.ultreon.devices.data;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.PrinterBlock;
import com.ultreon.devices.block.RouterBlock;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.init.DeviceItems;
import com.ultreon.devices.item.FlashDriveItem;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModRecipesProvider extends RecipeProvider {
    public ModRecipesProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        //***********************//
        //      Flash Drives     //
        //***********************//
        for (FlashDriveItem flashDrive : DeviceItems.getAllFlashDrives()) {
            DyeColor color = flashDrive.getColor();
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
                    .save(consumer);
        }

        //******************//
        //     Printers     //
        //******************//
        for (PrinterBlock printer : DeviceBlocks.getAllPrinters()) {
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
                    .save(consumer);
        }

        //*****************//
        //     Routers     //
        //*****************//
        for (RouterBlock router : DeviceBlocks.getAllRouters()) {
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
                    .save(consumer);
        }

//        //*************************//
//        //     Component Items     //
//        //*************************//
//        new ShapedRecipeBuilder(DeviceItems.COMPONENT_FLASH_CHIP.get(), 1)
//                .pattern("iri")
//                .pattern("ppp")
//                .pattern("ggg")
//                .define('p', DeviceItems.PLASTIC_FRAME.get())
//                .define('i', Items.IRON_INGOT)
//                .define('r', Items.REDSTONE)
//                .define('g', Items.GOLD_NUGGET)
//                .unlockedBy("has_laptop", has(ModTags.Items.LAPTOPS))
//                .save(consumer);

        new ShapelessRecipeBuilder(RecipeCategory.MISC, DeviceItems.COMPONENT_MOTHERBOARD_FULL, 1)
                .requires(DeviceItems.COMPONENT_CPU.get(), 1)
                .requires(DeviceItems.COMPONENT_GPU.get(), 1)
                .requires(DeviceItems.COMPONENT_RAM.get(), 1)
                .requires(DeviceItems.COMPONENT_MOTHERBOARD.get(), 1)
                .unlockedBy("has_motherboard", has(DeviceItems.COMPONENT_MOTHERBOARD.get()))
                .save(consumer);
    }
}
