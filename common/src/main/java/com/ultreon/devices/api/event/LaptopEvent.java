package com.ultreon.devices.api.event;

import com.ultreon.devices.block.entity.LaptopBlockEntity;
import org.jetbrains.annotations.NotNull;

public interface LaptopEvent extends ComputerEvent {
    @Override
    @NotNull LaptopBlockEntity getComputerBlockEntity();
}
