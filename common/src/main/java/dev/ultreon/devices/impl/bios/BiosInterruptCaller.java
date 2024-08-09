package dev.ultreon.devices.impl.bios;

import dev.ultreon.vbios.InterruptData;
import dev.ultreon.vbios.InterruptHandler;

public class BiosInterruptCaller {
    public InterruptHandler handler;

    public BiosInterruptCaller() {

    }

    public void trigger(InterruptData data) {
        handler.onInterrupt(data);
    }
}
