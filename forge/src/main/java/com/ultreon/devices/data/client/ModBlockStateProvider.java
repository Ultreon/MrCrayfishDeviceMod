package com.ultreon.devices.data.client;

import com.ultreon.devices.Devices;
import com.ultreon.devices.block.*;
import com.ultreon.devices.init.DeviceBlocks;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput gen, ExistingFileHelper exFileHelper) {
        super(gen, Devices.MOD_ID, exFileHelper);
    }

    @NotNull
    @Override
    public String getName() {
        return "Devices Mod - Block States and Models";
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected void registerStatesAndModels() {
        for (LaptopBlock block : DeviceBlocks.getAllLaptops()) {
            laptop(block);
        }
        for (PrinterBlock block : DeviceBlocks.getAllPrinters()) {
            printer(block);
        }
        for (RouterBlock block : DeviceBlocks.getAllRouters()) {
            router(block);
        }

        for (OfficeChairBlock block : DeviceBlocks.getAllOfficeChairs()) {
            officeChair(block);
        }
    }

    @Override
    public void simpleBlock(Block block) {
        try {
            super.simpleBlock(block);
        } catch (IllegalArgumentException e) {
            String name = Objects.requireNonNull(RegistrarManager.getId(block, Registries.BLOCK)).getPath();
            super.simpleBlock(block, models().cubeAll(name, modLoc("wip")));
        }
    }

    private void laptop(LaptopBlock block) {
        getVariantBuilder(block).forAllStates(state -> {
            String name = Objects.requireNonNull(RegistrarManager.getId(block, Registries.BLOCK)).getPath();
            var type = state.getValue(ComputerBlock.TYPE);
            var a = ConfiguredModel.builder();
            var q = a.modelFile(models().getBuilder(type == LaptopBlock.Type.BASE ? name : name + "_closed")
                    .parent(new ModelFile.UncheckedModelFile(modLoc(type == LaptopBlock.Type.BASE ? "block/laptop_base" : "block/laptop_screen")))
                    .texture("2", mcLoc("block/" + block.getColor().getName() + "_wool"))
            );
            q
                    .rotationY((int) state.getValue(LaptopBlock.FACING).toYRot())
                    .build();
            return a.build();
        });
    }

    private void officeChair(OfficeChairBlock block) {
        getVariantBuilder(block).forAllStates(state -> {
            String name = Objects.requireNonNull(RegistrarManager.getId(block, Registries.BLOCK)).getPath();
            var type = state.getValue(OfficeChairBlock.TYPE);
            var a = ConfiguredModel.builder();
            var q = a.modelFile(models().getBuilder(type == OfficeChairBlock.Type.SEAT ? name + "_seat" : type == OfficeChairBlock.Type.LEGS ? name + "_legs" : name + "_full")
                    .parent(new ModelFile.UncheckedModelFile(modLoc(type == OfficeChairBlock.Type.SEAT ? "block/office_chair_seat" : type == OfficeChairBlock.Type.LEGS ? "block/office_chair_legs" : "block/office_chair_full")))
                    .texture("chair_color", mcLoc("block/" + block.getColor().getName() + "_wool"))
            );
            q
                    .rotationY((int) state.getValue(LaptopBlock.FACING).toYRot())
                    .build();
            return a.build();
        });
    }

    private void printer(PrinterBlock block) {
        getVariantBuilder(block).forAllStates(state -> {
            String name = Objects.requireNonNull(RegistrarManager.getId(block, Registries.BLOCK)).getPath();
            return ConfiguredModel.builder()
                    .modelFile(models()
                            .getBuilder(name).parent(new ModelFile.UncheckedModelFile(modLoc("block/printer")))
                            .texture("2", mcLoc("block/" + block.getColor().getName() + "_wool")))
                    .rotationY((int) state.getValue(LaptopBlock.FACING).toYRot())
                    .build();
        });
    }

    private void router(RouterBlock block) {
        getVariantBuilder(block).forAllStates(state -> {
            String name = Objects.requireNonNull(RegistrarManager.getId(block, Registries.BLOCK)).getPath();
            return ConfiguredModel.builder()
                    .modelFile(models()
                            .getBuilder(name).parent(new ModelFile.UncheckedModelFile(modLoc("block/router")))
                            .texture("1", mcLoc("block/" + block.getColor().getName() + "_wool")))
                    .rotationY((int) state.getValue(LaptopBlock.FACING).toYRot())
                    .build();
        });
    }

    public ResourceLocation blockTexture(Block block) {
        ResourceLocation name = RegistrarManager.getId(block, Registries.BLOCK);
        return new ResourceLocation(Objects.requireNonNull(name).getNamespace(), "block/" + name.getPath());
    }
}
