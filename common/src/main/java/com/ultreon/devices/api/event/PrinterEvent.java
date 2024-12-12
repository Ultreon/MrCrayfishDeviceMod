package com.ultreon.devices.api.event;

import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.block.entity.DeviceBlockEntity;
import com.ultreon.devices.block.entity.PrinterBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PrinterEvent extends DeviceEvent {
    @NotNull PrinterBlockEntity getPrinter();

    @Nullable IPrint getPrint();

    @Override
    default @NotNull DeviceBlockEntity getDeviceBlockEntity() {
        return getPrinter();
    }
}
