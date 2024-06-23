package com.ultreon.devices.api.event;

import com.ultreon.devices.api.TrayItemAdder;
import com.ultreon.devices.core.Laptop;

public interface LaptopEvent {
    Event<SetupTrayItems> SETUP_TRAY_ITEMS = EventFactory.createLoop();

    interface SetupTrayItems extends LaptopEvent {
        void setupTrayItems(Laptop laptop, TrayItemAdder trayItems);
    }
}
