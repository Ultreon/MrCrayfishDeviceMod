package dev.ultreon.devices.impl.bios;

import de.waldheinz.fs.FileSystem;

public abstract class Disk {
    public static final long EFI_SIGNATURE = 0x545342465449L;

    public abstract FileSystem open();
}
