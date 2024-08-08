package dev.ultreon.devices.impl.bios;

import de.waldheinz.fs.BlockDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PartitionTable {
    public static final int MAX_PARTITIONS = 128;
    public List<PartitionEntry> entries;

    public PartitionTable(List<PartitionEntry> entries) {
        this.entries = entries;
    }

    public static PartitionTable load(BlockDevice blockDevice) throws IOException {
        long size = blockDevice.getSize();

        List<PartitionEntry> entries = new ArrayList<>();

        for (int i = 0; i < MAX_PARTITIONS; i++) {
            entries.add(PartitionEntry.read(blockDevice, size - (i + 1) * PartitionEntry.SIZE));
        }

        return new PartitionTable(entries);
    }
}
