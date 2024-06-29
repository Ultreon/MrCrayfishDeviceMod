package com.ultreon.devices.data.tags;

import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.NotNull;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(DataGenerator pGenerator) {
        super(pGenerator);
    }

    @NotNull
    @Override
    public String getName() {
        return "Devices Mod - Block Tags";
    }

    @Override
    protected void addTags() {
//        Builder<Block> laptops = this.tag(ModTags.Blocks.LAPTOPS);
//        Builder<Block> printers = this.tag(ModTags.Blocks.PRINTERS);
//        Builder<Block> routers = this.tag(ModTags.Blocks.ROUTERS);
//
//        DeviceBlocks.getAllLaptops().forEach(laptops::add);
//        DeviceBlocks.getAllPrinters().forEach(printers::add);
//        DeviceBlocks.getAllRouters().forEach(routers::add);
    }
}
