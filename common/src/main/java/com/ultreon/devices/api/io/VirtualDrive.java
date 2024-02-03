package com.ultreon.devices.api.io;

import com.ultreon.devices.api.driver.DiskDriver;
import com.ultreon.devices.api.driver.VirtualDiskDriver;
import com.ultreon.devices.core.Laptop;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;
import java.util.UUID;

public class VirtualDrive extends Drive {
    public VirtualDrive() {
        super("Virtual Drive", UUID.randomUUID(), Type.INTERNAL);
    }

    @Override
    public Optional<? extends DiskDriver> getDriver() {
        return Laptop.getInstance().getDriverManager().getByClass(VirtualDiskDriver.class);
    }
}
