package com.ultreon.devices.event;

import com.ultreon.devices.api.WorldSavedData;
import com.ultreon.devices.programs.email.EmailManager;
import com.ultreon.devices.programs.email.object.Email;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

//TODO
public class WorldDataHandler {
    private static final LevelResource DEVICES_MOD_DATA = new LevelResource("data/devices-mod");

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

    public WorldDataHandler() {
        LifecycleEvent.SERVER_STARTING.register(server -> {
            loadData(server.getWorldPath(DEVICES_MOD_DATA), "emails.dat", EmailManager.INSTANCE);
        });

        LifecycleEvent.SERVER_LEVEL_SAVE.register(level -> {
            saveData(level.getServer().getWorldPath(DEVICES_MOD_DATA), "emails.dat", EmailManager.INSTANCE);
        });

        LifecycleEvent.SERVER_STOPPED.register(server -> {
            EmailManager.INSTANCE.clear();
        });
    }

    private void loadData(Path modData, String fileName, WorldSavedData data) {
        if (Files.notExists(modData)) {
            return;
        }

        try(InputStream inputStream = Files.newInputStream(modData.resolve(fileName))) {
            CompoundTag nbt = NbtIo.readCompressed(inputStream);
            data.load(nbt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveData(Path modData, String fileName, WorldSavedData data) {
        try {
            Path resolve = modData.resolve(fileName);
            if (Files.notExists(modData)) {
                Files.createDirectories(modData);
                Files.createFile(resolve);
            }

            CompoundTag nbt = new CompoundTag();
            data.save(nbt);
            try (OutputStream output = Files.newOutputStream(resolve)) {
                NbtIo.writeCompressed(nbt, output);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
