package dev.ultreon.devices.impl.bios;

public record PowerModeInterrupt(
        PowerMode powerMode
) implements InterruptData {
    public enum PowerMode {
        SLEEP,
        HIBERNATE,
        REBOOT,
        SHUTDOWN
    }
}
