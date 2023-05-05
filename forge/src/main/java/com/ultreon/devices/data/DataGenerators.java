package com.ultreon.devices.data;

import com.ultreon.devices.Reference;
import com.ultreon.devices.data.client.ModBlockStateProvider;
import com.ultreon.devices.data.client.ModItemModelProvider;
import com.ultreon.devices.data.loot.ModLootTableProvider;
import net.minecraft.data.DataGenerator;
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
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        gen.addProvider(true, new ModBlockStateProvider(gen.getPackOutput(), existingFileHelper));
        gen.addProvider(true, new ModItemModelProvider(gen.getPackOutput(), existingFileHelper));
        gen.addProvider(true, new ModRecipesProvider(gen.getPackOutput()));
//        gen.addProvider(true, new ModLootTableProvider(gen.getPackOutput()));

        ///ModBlockTagsProvider blockTags = new ModBlockTagsProvider(gen);
       // gen.addProvider(blockTags);
       // gen.addProvider(new ModItemTagsProvider(gen, blockTags));
    }
}
