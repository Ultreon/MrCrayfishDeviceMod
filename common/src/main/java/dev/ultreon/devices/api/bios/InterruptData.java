package dev.ultreon.devices.api.bios;

public sealed interface InterruptData permits DoubleFaultInterrupt, FaultInterrupt, PowerModeInterrupt {

}
