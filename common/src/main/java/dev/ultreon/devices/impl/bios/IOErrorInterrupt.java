package dev.ultreon.devices.impl.bios;

import dev.ultreon.vbios.BiosInterruptType;

import java.io.IOException;

public class IOErrorInterrupt extends AbstractInterruptData {
    public final VEFI_SystemImpl vefiSystem;
    public final IOException e;

    public IOErrorInterrupt(VBios bios, VEFI_SystemImpl vefiSystem, IOException e) {
        super(bios, BiosInterruptType.IO_ERROR);
        this.vefiSystem = vefiSystem;
        this.e = e;
    }
}
