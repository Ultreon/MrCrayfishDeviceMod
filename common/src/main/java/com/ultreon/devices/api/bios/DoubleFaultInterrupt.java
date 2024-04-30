package com.ultreon.devices.api.bios;

import java.util.List;

public record DoubleFaultInterrupt(
        Throwable cause,
        List<Throwable> ignoredCauses,
        String message
) implements InterruptData {

}
