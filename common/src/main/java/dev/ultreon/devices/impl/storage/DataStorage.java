package dev.ultreon.devices.impl.storage;

import java.util.List;
import java.util.ServiceLoader;

public interface DataStorage {
    List<ServiceLoader.Provider<DataStorage>> STORAGES = ServiceLoader.load(DataStorage.class).stream().toList();

    static DataStorage findDataStorage(String id) {
        return STORAGES.stream().map(ServiceLoader.Provider::get).filter(storage -> storage.id().equals(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("No data storage found for id: " + id));
    }

    FileHandle get(String path);

    String id();
}
