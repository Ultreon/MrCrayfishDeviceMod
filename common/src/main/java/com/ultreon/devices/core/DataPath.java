package com.ultreon.devices.core;

import com.ultreon.devices.api.io.Drive;

import java.nio.file.Path;
import java.util.UUID;

public record DataPath(UUID drive, Path path) {
    public DataPath(Drive drive, Path resolve) {
        this(drive.getUUID(), resolve);
    }
}
