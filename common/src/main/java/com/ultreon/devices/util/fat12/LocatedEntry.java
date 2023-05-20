package com.ultreon.devices.util.fat12;

import de.waldheinz.fs.fat.FatLfnDirectory;
import de.waldheinz.fs.fat.FatLfnDirectoryEntry;

public record LocatedEntry(FatLfnDirectory parent, FatLfnDirectoryEntry current) {
}
