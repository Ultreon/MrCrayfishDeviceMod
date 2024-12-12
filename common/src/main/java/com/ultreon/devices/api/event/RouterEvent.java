package com.ultreon.devices.api.event;

import com.ultreon.devices.block.entity.DeviceBlockEntity;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.core.network.Router;
import org.jetbrains.annotations.NotNull;

public interface RouterEvent extends DeviceEvent {
    @NotNull Router getRouter();

    @NotNull RouterBlockEntity getRouterBlockEntity();

    default @NotNull DeviceBlockEntity getDeviceBlockEntity() {
        return getRouterBlockEntity();
    }
}
