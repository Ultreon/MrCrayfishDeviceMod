package dev.ultreon.devices.api.bios;

public record FaultInterrupt(
        Throwable cause,
        String message
) implements InterruptData {

}
