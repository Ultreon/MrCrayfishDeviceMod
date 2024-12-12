package com.ultreon.devices.api.event;

import com.ultreon.devices.block.entity.ComputerBlockEntity;
import com.ultreon.devices.block.entity.DeviceBlockEntity;
import org.jetbrains.annotations.NotNull;

public interface ComputerEvent extends DeviceEvent {
    @NotNull ComputerBlockEntity getComputerBlockEntity();

    @Override
    default @NotNull DeviceBlockEntity getDeviceBlockEntity() {
        return getComputerBlockEntity();
    }
}
