package dev.ultreon.devices.api.bios;

@FunctionalInterface
public interface InterruptHandler {
    void onInterrupt(InterruptData interrupt);
}
