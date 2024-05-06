package dev.ultreon.devices.api.bios;

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
