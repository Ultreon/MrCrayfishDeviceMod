package com.ultreon.devices.datagen;

import com.ultreon.devices.init.DeviceItems;
import com.ultreon.devices.init.DeviceTags;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class DevicesItemTagProvider extends FabricTagProvider<Item> {
    public DevicesItemTagProvider(FabricDataOutput dataGenerator) {
        super(dataGenerator, Registries.ITEM, CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor()));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        TagAppender<Item> tag = tag(DeviceTags.Items.LAPTOPS);
        for (RegistrySupplier<Item> laptop : DeviceItems.LAPTOPS) {
            tag.addOptional(laptop.getId());
        }
    }
}
