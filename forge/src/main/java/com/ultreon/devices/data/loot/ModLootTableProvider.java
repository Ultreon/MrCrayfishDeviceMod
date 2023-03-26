package com.ultreon.devices.data.loot;

import com.google.common.collect.ImmutableList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(PackOutput arg, Set<ResourceLocation> set, List<SubProviderEntry> list) {
        super(arg, set, list);
    }

    @NotNull
    @Override
    public List<SubProviderEntry> getTables() {
        return ImmutableList.of(new SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, @NotNull ValidationContext ctx) {
        map.forEach((id, table) -> LootTables.validate(ctx, id, table));
    }
}
