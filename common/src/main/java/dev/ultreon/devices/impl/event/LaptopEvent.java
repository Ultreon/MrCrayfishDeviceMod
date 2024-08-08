package dev.ultreon.devices.impl.event;

import dev.ultreon.devices.impl.TrayItemAdder;
import dev.ultreon.mineos.userspace.MineOS;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface LaptopEvent {
    Event<SetupTrayItems> SETUP_TRAY_ITEMS = EventFactory.createLoop();

    interface SetupTrayItems extends LaptopEvent {
        void setupTrayItems(MineOS laptop, TrayItemAdder trayItems);
    }
}
