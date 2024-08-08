package dev.ultreon.devices.impl.bios;

public interface BiosInterrupt<T extends InterruptData> {
    void interrupt(T data);
}
