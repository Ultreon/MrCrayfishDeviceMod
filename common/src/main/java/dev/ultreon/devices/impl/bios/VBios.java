package dev.ultreon.devices.impl.bios;

import com.google.gson.Gson;
import de.waldheinz.fs.*;
import de.waldheinz.fs.fat.FatFileSystem;
import dev.ultreon.devices.api.bios.*;
import dev.ultreon.vbios.*;
import dev.ultreon.vbios.Bios;
import dev.ultreon.vbios.InterruptData;
import dev.ultreon.vbios.efi.VEFI_DiskInfo;
import dev.ultreon.vbios.efi.VEFI_File;
import dev.ultreon.vbios.efi.VEFI_System;
import dev.ultreon.devices.api.device.HardwareDevice;
import dev.ultreon.devices.api.device.VEFI_Disk;
import dev.ultreon.devices.impl.device.HostDevice;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileExistsException;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class VBios implements Bios {
    private static final int PB_POWER_OFF = 0x00;
    private final BiosInterruptCaller[] interrupts = new BiosInterruptCaller[BiosInterruptType.values().length];
    private final HostDevice hostDevice;
    private final VEFI_SystemImpl system;
    private int faults = 0;
    private UUID bootDeviceId;
    private BlockDevice blockDevice;
    private VirtVGA graphics;
    private FileSystem fileSystem;
    private Map<UUID, VirtualDisk> virtualDisks = new LinkedHashMap<>();
    private Gson gson = new Gson();

    public VBios(HostDevice hostDevice) {
        this.hostDevice = hostDevice;
        for (int i = 0; i < interrupts.length; i++) {
            interrupts[i] = new BiosInterruptCaller();
        }

        this.system = new VEFI_SystemImpl(hostDevice.getDevices(), this);

        try {
            VEFI_DiskInfo[] driveList = this.system.getDriveList();
            for (VEFI_DiskInfo drive : driveList) {
                if (drive.deviceID().isDrive()) {
                    this.attemptBoot(drive);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void attemptBoot(VEFI_DiskInfo diskInfo) {
        VEFI_Disk vefiDisk = this.system.openDisk(diskInfo);

        if (vefiDisk != null) {
            this.bootDeviceId = diskInfo.deviceID().id();
            if (this.virtualDisks.containsKey(this.bootDeviceId)) {
                VirtualDisk virtualDisk = this.virtualDisks.get(this.bootDeviceId);
                this.fileSystem = virtualDisk;
                this.graphics = new VirtVGA(this.hostDevice.getGraphics());
                return;
            }
            BlockDevice blockDevice = this.system.disks[vefiDisk.handle()].blockDevice();
            try {
                PartitionTable partitionTable = PartitionTable.load(blockDevice);
                for (PartitionEntry entry : partitionTable.entries) {
                    this.fileSystem = FatFileSystem.read(entry.open(blockDevice), true);
                }
                this.graphics = new VirtVGA(this.hostDevice.getGraphics());
            } catch (IOException e) {
                try {
                    this.system.closeDisk(vefiDisk);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    @Override
    public void registerInterrupt(BiosInterruptType interrupt, InterruptHandler handler) {
        this.interrupts[interrupt.ordinal()].handler = handler;
    }

    @Override
    public Object call(BiosCallType call, Object[] args) {
        return switch (call) {
            case REBOOT -> {
                this.reset();
                yield null;
            }
            case POWER_OFF -> {
                this.powerOff();
                yield null;
            }
            case ADD_SYSTEM -> {
                VEFI_File file = (VEFI_File) args[0];
                int handle = file.handle();

                VEFI_FileContext fileContext = this.system.getFile(handle);
                byte[] data = new byte[32];
                byte[] bytes = fileContext.fileInfo().getBytes();
                ByteBuffer biosData = this.hostDevice.getBiosData();
                biosData.position(0xf00);
                byte b = biosData.get(0xeff);
                biosData.put(bytes, 0xf00 + b * 32, min(32, bytes.length));
                yield null;
            }
            case GET_BOOT_DEVICE_ID -> this.getBootDeviceId();
            case FRAMEBUFFER_CALL -> {
                FrameBufferCall callId = (FrameBufferCall) args[0];
                Object[] data = (Object[]) args[1];

                yield this.graphics.call(callId, data);
            }
            case ENTER_USERSPACE -> {
                String path = (String) args[0];
                ClassLoader parent = (ClassLoader) args[2];
                this.enterUserspace(parent, Paths.get(path));
                yield null;
            }
            case ENTER_SLEEP -> {
                this.interrupt(new AbstractInterruptData(this, BiosInterruptType.FRAMEBUFFER_INTERRUPT) {
                    @SuppressWarnings("unused")
                    public final int type = FrameBuffer.DISABLE;
                });
                this.graphics.turnOff();
                this.hostDevice.enterSleep();
                yield null;
            }
            case EXIT_SLEEP -> {
                this.interrupt(new AbstractInterruptData(this, BiosInterruptType.FRAMEBUFFER_INTERRUPT) {
                    @SuppressWarnings("unused")
                    public final int type = FrameBuffer.ENABLE;
                });
                this.graphics.reset();
                this.hostDevice.exitSleep();
                yield null;
            }
            default -> null;
        };
    }

    private void enterUserspace(ClassLoader parent, Path executable) {
        if (!(parent instanceof IsolatedClassLoader isolation)) {
            fault(new Exception("Invalid parent classloader"));
            return;
        }

        try {
            Executable exec = readExecutable(this.openFile(executable));

            UserspaceClassLoader classLoader = new UserspaceClassLoader(isolation, this, exec);
            exec.execute(classLoader, new String[0]);
        } catch (Throwable e) {
            fault(e);
        }
    }

    private Executable readExecutable(SeekableByteChannel inputStream) {
        try {
            ZipFile zipFile = new ZipFile(inputStream);
            ExecutableResource resource = new ExecutableResource(zipFile);
            ZipArchiveEntry entry = zipFile.getEntry(".meta");
            InputStream inputStream1 = zipFile.getInputStream(entry);
            ExecMeta execMeta = gson.fromJson(new InputStreamReader(inputStream1), ExecMeta.class);
            return new Executable(execMeta, resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SeekableByteChannel openFile(Path executable) throws IOException {
        FsFile file = this.getFile(executable);
        return new FSFileChannel(file);
    }

    private FsFile getFile(Path path) throws IOException {
        FsDirectoryEntry root = this.getFsDirectoryEntry(path);

        if (root instanceof FsFile) {
            return (FsFile) root;
        }

        throw new FileNotFoundException(path.toString());
    }

    private UUID getBootDeviceId() {
        return this.bootDeviceId;
    }

    private void powerOff() {
        this.powerDownImmediately();
    }

    @Override
    public void enableInterrupts() {

    }

    @Override
    public void disableInterrupts() {

    }

    @Override
    public VEFI_System getVEFISystem() {
        return this.system;
    }

    public void interrupt(InterruptData data) {
        BiosInterruptType biosInterruptType = data.interruptType();
        if (biosInterruptType == BiosInterruptType.FAULT) {
            if (faults > 10) {
                this.reset();
            }
            if (faults > 0) {
                biosInterruptType = BiosInterruptType.DOUBLE_FAULT;
            }

            faults++;
        }

        interrupts[biosInterruptType.ordinal()].trigger(data);
    }

    private void reset() {
        this.powerDownImmediately();
    }

    private void powerDownImmediately() {
        this.hostDevice.powerOff();
    }

    public void fault(Throwable e) {
        if (faults > 10) this.reset();
        if (faults++ > 0) {
            DoubleFaultInterrupt doubleFaultInterrupt = new DoubleFaultInterrupt(this);
            doubleFaultInterrupt.errorCode = 1;
            doubleFaultInterrupt.stackTrace = Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n"));
            doubleFaultInterrupt.cause = e;
            this.interrupt(doubleFaultInterrupt);
            return;
        }
        FaultInterrupt faultInterrupt = new FaultInterrupt(this);
        faultInterrupt.errorCode = 1;
        faultInterrupt.stackTrace = Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n"));
        faultInterrupt.cause = e;
        this.interrupt(faultInterrupt);
    }

    public void requestPowerOff() {
        this.interrupt(new AbstractInterruptData(this, BiosInterruptType.POWER_BUTTON) {
            @SuppressWarnings("unused")
            public final int type = PB_POWER_OFF;
        });
    }

    public HardwareDevice getPhysicalDevice(UUID id) {
        return hostDevice.getPhysicalDevice(id);
    }

    public VEFI_SystemImpl getSystem() {
        return system;
    }

    public FrameBuffer getFrameBuffer() {
        return graphics.getFrameBuffer();
    }

    public byte[] getUserspaceClass(String path) throws IOException {
        Path file = Paths.get(path);
        FsDirectoryEntry current = getFsDirectoryEntry(file);

        if (current instanceof FsFile fsFile) {
            ByteBuffer buffer = ByteBuffer.allocate((int) fsFile.getLength());
            fsFile.read(0L, buffer);
            buffer.flip();

            return buffer.array();
        }
        throw new FileNotFoundException(path);
    }

    private @Nullable FsDirectoryEntry getFsDirectoryEntry(Path file) throws IOException {
        FsDirectory root = this.fileSystem.getRoot();
        FsDirectoryEntry current = null;

        StringBuilder curPath = new StringBuilder("/");

        for (Path p : file) {
            String name = p.getFileName().toString();
            if (current == null) {
                current = root.getEntry(name);
                if (current.isDirectory()) {
                    curPath.append(name).append("/");
                } else {
                    curPath.append(name);
                }
            } else if (current.isDirectory() && current instanceof FsDirectory) {
                current = ((FsDirectory) current).getEntry(name);
            } else {
                throw new FileExistsException(curPath.toString());
            }
            if (current == null) {
                throw new FileNotFoundException(curPath.toString());
            }
        }
        return current;
    }

    private static class FaultInterrupt extends AbstractInterruptData {
        public int errorCode = 0;
        public @NullPtr String stackTrace = "";
        public @NullPtr Throwable cause;

        public FaultInterrupt(VBios vbios) {
            super(vbios, BiosInterruptType.FAULT);
        }
    }

    private static class DoubleFaultInterrupt extends AbstractInterruptData {
        public int errorCode = 0;
        public @NullPtr String stackTrace = "";
        public @NullPtr Throwable cause;

        public DoubleFaultInterrupt(VBios vbios) {
            super(vbios, BiosInterruptType.DOUBLE_FAULT);
        }
    }

    private static class FSFileChannel implements SeekableByteChannel {
        private final FsFile file;
        private long position;
        private boolean open;

        public FSFileChannel(FsFile file) {
            this.file = file;
            this.position = 0;
        }

        @Override
        public void close() throws IOException {
            if (this.open) {
                this.open = false;
            } else {
                throw new AlreadyClosedException();
            }
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            if (!this.open) throw new ClosedChannelException();
            long endOff = this.position + dst.remaining();
            if (this.position >= this.file.getLength()) return -1;
            this.file.read(this.position, dst);
            if (endOff > this.file.getLength()) {
                this.position = this.file.getLength();
                return (int) (endOff - this.file.getLength());
            }

            this.position = endOff;
            return dst.remaining();
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            if (!this.open) throw new ClosedChannelException();
            this.file.write(this.position, src);
            this.position += src.remaining();
            return src.remaining();
        }

        @Override
        public long position() throws IOException {
            return this.position;
        }

        @Override
        public SeekableByteChannel position(long newPosition) throws IOException {
            this.position = newPosition;
            return this;
        }

        @Override
        public long size() throws IOException {
            return this.file.getLength();
        }

        @Override
        public SeekableByteChannel truncate(long size) throws IOException {
            throw new IOException("Not supported");
        }

        @Override
        public boolean isOpen() {
            return open;
        }
    }
}
