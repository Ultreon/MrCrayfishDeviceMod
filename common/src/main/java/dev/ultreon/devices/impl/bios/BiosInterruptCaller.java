package dev.ultreon.devices.impl.bios;

import dev.ultreon.devices.api.bios.InterruptData;
import dev.ultreon.devices.api.bios.InterruptHandler;

public class BiosInterruptCaller {
    public InterruptHandler handler;

    public BiosInterruptCaller() {

    }

    public void trigger(InterruptData data) {
        handler.onInterrupt(data);
    }
}
