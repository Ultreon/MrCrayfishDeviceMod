package com.ultreon.devices.data.tags;

import com.ultreon.devices.Devices;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.init.ModTags;
import dev.architectury.registry.registries.Registrar;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends VanillaBlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @NotNull
    @Override
    public String getName() {
        return "Devices Mod - Block Tags";
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider arg) {
        Registrar<Block> blocks = Devices.REGISTRIES.get().get(Registries.BLOCK);
        TagAppender<Block> laptops = this.tag(ModTags.Blocks.LAPTOPS);
        TagAppender<Block> printers = this.tag(ModTags.Blocks.PRINTERS);
        TagAppender<Block> routers = this.tag(ModTags.Blocks.ROUTERS);

        DeviceBlocks.getAllLaptops().forEach(o -> laptops.addOptional(Objects.requireNonNull(blocks.getId(o))));
        DeviceBlocks.getAllPrinters().forEach(o -> printers.addOptional(Objects.requireNonNull(blocks.getId(o))));
        DeviceBlocks.getAllRouters().forEach(o -> routers.addOptional(Objects.requireNonNull(blocks.getId(o))));
    }
}
