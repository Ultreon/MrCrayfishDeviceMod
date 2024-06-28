package com.ultreon.devices.client;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.network.NetworkEvent;

import static com.ultreon.devices.Devices.LOGGER;

public class DevicesClient {
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(DevicesClient::playerQuit);
    }

    private static void playerQuit(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        Devices.logOut();
    }
}
