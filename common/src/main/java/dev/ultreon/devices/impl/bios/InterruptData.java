package dev.ultreon.devices.impl.bios;

public sealed interface InterruptData permits DoubleFaultInterrupt, FaultInterrupt, PowerModeInterrupt {

}
