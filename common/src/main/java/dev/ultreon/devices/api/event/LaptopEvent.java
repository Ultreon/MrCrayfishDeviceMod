package dev.ultreon.devices.api.event;

import dev.ultreon.devices.api.TrayItemAdder;
import dev.ultreon.devices.mineos.client.MineOS;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface LaptopEvent {
    Event<SetupTrayItems> SETUP_TRAY_ITEMS = EventFactory.createLoop();

    interface SetupTrayItems extends LaptopEvent {
        void setupTrayItems(MineOS laptop, TrayItemAdder trayItems);
    }
}
