package com.ultreon.devices.event;

import com.ultreon.devices.api.WorldSavedData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.storage.FolderName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
//TODO
public class WorldDataHandler {
    private static final FolderName DEVICES_MOD_DATA = new FolderName("data/devices-mod");

//    @SubscribeEvent
//    public void load(final LifecycleEvent.START event) {
////        LifecycleEvent.SERVER_STARTING;
//        final File modData = Objects.requireNonNull(event.getServer(), "World loaded without server").getWorldPath(DEVICES_MOD_DATA).toFile();
//        if (!modData.exists()) {
//            try {
//                Files.createDirectories(modData.toPath());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        loadData(modData, "emails.dat", EmailManager.INSTANCE);
//        loadData(modData, "bank.dat", BankUtil.INSTANCE);
//    }

//    @SubscribeEvent
//    public void save(final WorldEvent.Save event) {
//        LifecycleEvent.SERVER_LEVEL_SAVE
//        final MinecraftServer server = event.getWorld().getServer();
//        if (server == null) {
//            if (event.getWorld().getLevelData() instanceof ServerLevelData serverLevelData)
//                DevicesMod.LOGGER.warn("World {} saved without server", serverLevelData.getLevelName());
//            else
//                DevicesMod.LOGGER.warn("World saved without server");
//            return;
//        }
//        File modData = server.getWorldPath(DEVICES_MOD_DATA).toFile();
//        if (!modData.exists()) {
//            try {
//                Files.createDirectories(modData.toPath());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        saveData(modData, "emails.dat", EmailManager.INSTANCE);
//        saveData(modData, "bank.dat", BankUtil.INSTANCE);
//    }

    private void loadData(File modData, String fileName, WorldSavedData data) {
        File dataFile = new File(modData, fileName);
        if (!dataFile.exists()) {
            return;
        }
        try {
            CompoundNBT nbt = CompressedStreamTools.readCompressed(dataFile);
            data.load(nbt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveData(File modData, String fileName, WorldSavedData data) {
        try {
            File dataFile = new File(modData, fileName);
            if (!dataFile.exists()) {
                Files.createFile(dataFile.toPath());
            }

            CompoundNBT nbt = new CompoundNBT();
            data.save(nbt);
            CompressedStreamTools.writeCompressed(nbt, dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
