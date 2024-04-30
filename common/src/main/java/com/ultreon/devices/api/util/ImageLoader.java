package com.ultreon.devices.api.util;

import com.ultreon.devices.api.storage.FileHandle;

import java.util.List;
import java.util.ServiceLoader;

public interface ImageLoader {
    List<ServiceLoader.Provider<ImageLoader>> LOADERS = ServiceLoader.load(ImageLoader.class).stream().toList();

    ImageSource load(FileHandle handle);
}
