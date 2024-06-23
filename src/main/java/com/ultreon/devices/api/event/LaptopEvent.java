package com.ultreon.devices.api.event;

import com.ultreon.devices.api.TrayItemAdder;
import com.ultreon.devices.core.Laptop;
import net.minecraftforge.eventbus.api.Event;

public class LaptopEvent extends Event {
    private final Laptop laptop;
    private final TrayItemAdder trayItems;

    public LaptopEvent(Laptop laptop, TrayItemAdder trayItems) {
        this.laptop = laptop;
        this.trayItems = trayItems;
    }

    public Laptop getLaptop() {
        return laptop;
    }

    public TrayItemAdder getTrayItems() {
        return trayItems;
    }
}
