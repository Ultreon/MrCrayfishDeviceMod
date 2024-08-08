package dev.ultreon.devices.impl.bios;

public record FaultInterrupt(
        Throwable cause,
        String message
) implements InterruptData {

}
