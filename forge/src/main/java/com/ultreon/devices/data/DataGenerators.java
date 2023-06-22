package com.ultreon.devices.data;

import com.ultreon.devices.Reference;
import com.ultreon.devices.data.client.ModBlockStateProvider;
import com.ultreon.devices.data.client.ModItemModelProvider;
import com.ultreon.devices.data.loot.ModLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class DataGenerators {
    private DataGenerators() {
        throw new UnsupportedOperationException("Can't instantiate utility class");
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        gen.addProvider(true, new ModBlockStateProvider(packOutput, existingFileHelper));
        gen.addProvider(true, new ModItemModelProvider(packOutput, existingFileHelper));
        gen.addProvider(true, new ModRecipesProvider(packOutput));
        gen.addProvider(true, new ModLootTableProvider(packOutput, Set.of(), VanillaLootTableProvider.create(packOutput).getTables()));

        ///ModBlockTagsProvider blockTags = new ModBlockTagsProvider(gen);
       // gen.addProvider(blockTags);
       // gen.addProvider(new ModItemTagsProvider(gen, blockTags));
    }
}
