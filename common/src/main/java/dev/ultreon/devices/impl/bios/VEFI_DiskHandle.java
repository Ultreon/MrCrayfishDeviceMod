package dev.ultreon.devices.impl.bios;

import de.waldheinz.fs.BlockDevice;
import dev.ultreon.vbios.efi.VEFI_File;
import dev.ultreon.devices.api.device.HardwareDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

public final class VEFI_DiskHandle {
    private final VEFI_SystemImpl system;
    private final HardwareDevice hardwareDevice;
    private final BlockDevice blockDevice;
    private final int handle;
    BitSet openFiles = new BitSet(128);
    VEFI_File[] files = new VEFI_File[128];
    private BlockDevice bootPartition = null;

    public VEFI_DiskHandle(VEFI_SystemImpl system, HardwareDevice hardwareDevice, BlockDevice blockDevice, int handle) {
        this.system = system;
        this.hardwareDevice = hardwareDevice;
        this.blockDevice = blockDevice;
        this.handle = handle;
    }

    public BlockDevice blockDevice() {
        return blockDevice;
    }

    public HardwareDevice hardwareDevice() {
        return hardwareDevice;
    }

    public int handle() {
        return handle;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (VEFI_DiskHandle) obj;
        return Objects.equals(this.hardwareDevice, that.hardwareDevice) &&
               this.handle == that.handle;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hardwareDevice, handle);
    }

    @Override
    public String toString() {
        return "VEFI_DiskHandle[" +
               "hardwareDevice=" + hardwareDevice + ", " +
               "handle=" + handle + ']';
    }

    void findBootPartition(PartitionEntry entry) {
        this.bootPartition = entry.open(blockDevice);
    }

    public BlockDevice getBootPartition() {
        return bootPartition;
    }

    public void close() throws IOException {
        List<Exception> suppressed = new ArrayList<>();
        for (VEFI_File file : files) {
            if (file != null) {
                system.closeFile(file);
            }
        }

        if (bootPartition != null) {
            try {
                bootPartition.close();
            } catch (Exception e) {
                suppressed.add(e);
            }
        }

        if (blockDevice != null) {
            try {
                blockDevice.close();
            } catch (Exception e) {
                suppressed.add(e);
            }
        }

        if (!suppressed.isEmpty()) {
            IOException exception = new IOException("Failed to close VEFI_DiskHandle");
            suppressed.forEach(exception::addSuppressed);
            throw exception;
        }
    }
}
