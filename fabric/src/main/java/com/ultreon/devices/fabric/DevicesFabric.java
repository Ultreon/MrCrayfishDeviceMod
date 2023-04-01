package com.ultreon.devices.fabric;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import com.ultreon.devices.init.RegistrationHandler;
import net.fabricmc.api.ModInitializer;

public class DevicesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        DeviceConfig.register(null);
        Devices.init();
        RegistrationHandler.register();
    }
}