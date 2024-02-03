package com.ultreon.devices.api.driver;

public class DeviceDrivers {
    public static final DriverFactory<PhysicalDiskDriver> PHYSICAL_DISK = DriverFactory.of(PhysicalDiskDriver::new);
    public static final DriverFactory<VirtualDiskDriver> VIRTUAL_DISK = DriverFactory.of(VirtualDiskDriver::new);
}
