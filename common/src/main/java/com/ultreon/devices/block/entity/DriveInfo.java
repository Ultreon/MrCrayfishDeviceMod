package com.ultreon.devices.block.entity;

import com.ultreon.devices.api.io.Drive;

import java.util.UUID;

public record DriveInfo(
    String name,
    UUID uuid,
    Drive.Type type,
    boolean main
) {
    public DriveInfo(String name, UUID uuid, Drive.Type type) {
        this(name, uuid, type, false);
    }
}
