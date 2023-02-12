package com.ultreon.devices.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DevicesDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(DevicesModelGenerator::new);
        fabricDataGenerator.addProvider(DevicesLanguageGenerator::new);
        fabricDataGenerator.addProvider(new DevicesLanguageGenerator(fabricDataGenerator, "en_au"));
        fabricDataGenerator.addProvider(new DevicesLanguageGenerator(fabricDataGenerator, "en_pt"));
        fabricDataGenerator.addProvider(new DevicesLanguageGenerator(fabricDataGenerator, "lol_us"));
        fabricDataGenerator.addProvider(new DevicesLanguageGenerator(fabricDataGenerator, "ja_jp"));
        fabricDataGenerator.addProvider(DevicesRecipeProvider::new);
    }
}
