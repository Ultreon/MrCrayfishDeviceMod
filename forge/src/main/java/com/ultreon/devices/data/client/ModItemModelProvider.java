package com.ultreon.devices.data.client;

import com.ultreon.devices.Reference;
import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.block.PrinterBlock;
import com.ultreon.devices.block.RouterBlock;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.init.DeviceItems;
import com.ultreon.devices.item.FlashDriveItem;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Reference.MOD_ID, existingFileHelper);
    }

    @Override
    public @NotNull
    String getName() {
        return "Devices Mod - Item Models";
    }

    @Override
    protected void registerModels() {
        DeviceBlocks.getAllBlocks().filter((block) -> block != DeviceBlocks.PAPER.get() && block.getClass().getPackage().getName().startsWith("com.mrcrayfish.device")).forEach(this::blockBuilder);

        ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));
        ModelFile itemHandheld = getExistingFile(mcLoc("item/handheld"));

//        getBuilder(DeviceItems.PAPER.get(), itemGenerated, "model/paper").transforms().transform(ItemTransforms.TransformType.FIXED).rotation(0, 0, 0).translation(0, 0, 0).scale(0.5f, 0.5f, 0.5f).end().end();

        for (FlashDriveItem flashDrive : DeviceItems.getAllFlashDrives()) {
            flashDrive(flashDrive);
        }
        for (LaptopBlock laptopBlock : DeviceBlocks.getAllLaptops()) {
            blockBuilder(laptopBlock);
        }
        for (PrinterBlock printerBlock : DeviceBlocks.getAllPrinters()) {
            blockBuilder(printerBlock);
        }
        for (RouterBlock routerBlock : DeviceBlocks.getAllRouters()) {
            blockBuilder(routerBlock);
        }
    }

    private void flashDrive(FlashDriveItem flashDrive) {
        getBuilder(Objects.requireNonNull(RegistrarManager.getId(flashDrive, Registries.ITEM)).getPath()).parent(getExistingFile(modLoc("item/flash_drive"))).texture("1", mcLoc("block/" + flashDrive.getColor().getSerializedName() + "_wool"));
    }

    private void blockBuilder(Block block) {
        try {
            String name = Objects.requireNonNull(RegistrarManager.getId(block, Registries.BLOCK)).getPath();
            withExistingParent(name, modLoc("block/" + name));
        } catch (IllegalStateException ignored) {

        }
    }

    private void builder(ItemLike item, ModelFile parent) {
        String name = Objects.requireNonNull(RegistrarManager.getId(item.asItem(), Registries.ITEM)).getPath();
        builder(item, parent, "item/" + name);
    }

//    private ItemModelBuilder getBuilder(ItemLike item, ModelFile parent) {
//        String name = Objects.requireNonNull(item.asItem().getRegistriesName()).getPath();
//        return getBuilder(item, parent, "item/" + name);
//    }

    private void builder(ItemLike item, ModelFile parent, String texture) {
        try {
            getBuilder(Objects.requireNonNull(RegistrarManager.getId(item.asItem(), Registries.ITEM)).getPath())
                    .parent(parent)
                    .texture("layer0", modLoc(texture));
        } catch (IllegalArgumentException e) {
            getBuilder(Objects.requireNonNull(RegistrarManager.getId(item.asItem(), Registries.ITEM)).getPath())
                    .parent(getExistingFile(mcLoc("item/generated")))
                    .texture("layer0", modLoc("wip"));
        }
    }

//    private ItemModelBuilder getBuilder(ItemLike item, ModelFile parent, String texture) {
//        return getBuilder(Objects.requireNonNull(item.asItem().getRegistriesName()).getPath())
//                .parent(parent)
//                .texture("layer0", modLoc(texture));
//    }
//
//    private ItemModelBuilder getBuilder(ItemLike item, ModelFile parent, String modelPath, String texture) {
//        return getBuilder(modelPath)
//                .parent(parent)
//                .texture("layer0", modLoc(texture));
//    }
}
