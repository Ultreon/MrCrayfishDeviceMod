package com.ultreon.devices.data.tags;

import com.ultreon.devices.Devices;
import com.ultreon.devices.init.DeviceItems;
import com.ultreon.devices.init.ModTags;
import dev.architectury.registry.registries.Registrar;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture, TagsProvider<Block> arg2) {
        super(arg, completableFuture, arg2);
    }

    @NotNull
    @Override
    public String getName() {
        return "Devices Mod - Item Tags";
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider arg) {
        Registrar<Item> items = Devices.REGISTRIES.get().get(Registries.ITEM);
        TagAppender<Item> laptops = this.tag(ModTags.Items.LAPTOPS);
        TagAppender<Item> printers = this.tag(ModTags.Items.PRINTERS);
        TagAppender<Item> routers = this.tag(ModTags.Items.ROUTERS);
        TagAppender<Item> flashDrives = this.tag(ModTags.Items.FLASH_DRIVES);

        DeviceItems.getAllLaptops().forEach(o -> laptops.addOptional(Objects.requireNonNull(items.getId(o))));
        DeviceItems.getAllPrinters().forEach(o -> printers.addOptional(Objects.requireNonNull(items.getId(o))));
        DeviceItems.getAllRouters().forEach(o -> routers.addOptional(Objects.requireNonNull(items.getId(o))));
        DeviceItems.getAllFlashDrives().forEach(o -> flashDrives.addOptional(Objects.requireNonNull(items.getId(o))));
    }
}
