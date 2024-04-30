package com.ultreon.devices.api.bios;

public interface BiosInterrupt<T extends InterruptData> {
    void interrupt(T data);

}
