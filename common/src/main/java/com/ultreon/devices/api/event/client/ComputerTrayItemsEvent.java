package com.ultreon.devices.api.event.client;

import com.ultreon.devices.api.TrayItemAdder;
import com.ultreon.devices.core.ComputerScreen;

public class ComputerTrayItemsEvent implements ComputerUIEvent {
    private final ComputerScreen computerScreen;
    private final TrayItemAdder trayItems;

    public ComputerTrayItemsEvent(ComputerScreen computerScreen, TrayItemAdder trayItems) {
        this.computerScreen = computerScreen;
        this.trayItems = trayItems;
    }

    @Override
    public ComputerScreen getLaptop() {
        return computerScreen;
    }

    public TrayItemAdder getTrayItems() {
        return trayItems;
    }
}
