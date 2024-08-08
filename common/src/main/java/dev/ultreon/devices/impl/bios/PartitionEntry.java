package dev.ultreon.devices.impl.bios;

import de.waldheinz.fs.BlockDevice;
import de.waldheinz.fs.ReadOnlyException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class PartitionEntry {
    public static final int SIZE = 296;
    public long start;
    public long end;
    public byte type;
    public UUID guid;
    public String name;
    public boolean readOnlyStatus;

    public static PartitionEntry read(BlockDevice device, long offset) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        device.read(offset, buffer);

        buffer.flip();

        PartitionEntry partitionEntry = new PartitionEntry();
        partitionEntry.start = offset;
        partitionEntry.end = buffer.getInt();
        partitionEntry.type = buffer.get();
        partitionEntry.guid = new UUID(buffer.getLong(), buffer.getLong());
        partitionEntry.name = getString(buffer, 16);
        partitionEntry.readOnlyStatus = buffer.get() == 1;

        return partitionEntry;
    }

    private static String getString(ByteBuffer buffer, int len) {
        byte[] bytes = new byte[len];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    public BlockDevice open(BlockDevice parent) {
        return new PartitionBlockDevice(this, parent);
    }

    private static class PartitionBlockDevice implements BlockDevice {
        private final PartitionEntry partitionEntry;
        private final BlockDevice parent;
        private boolean closed;

        public PartitionBlockDevice(PartitionEntry partitionEntry, BlockDevice parent) {
            this.partitionEntry = partitionEntry;
            this.parent = parent;
        }

        @Override
        public long getSize() {
            return this.partitionEntry.end - this.partitionEntry.start;
        }

        @Override
        public void read(long devOffset, ByteBuffer dest) throws IOException {
            if (closed) throw new ClosedChannelException();
            if (devOffset > this.getSize())
                throw new IOException("IO address overflow: " + devOffset + " > " + this.getSize());
            this.parent.read(this.partitionEntry.start + devOffset, dest);
        }

        @Override
        public void write(long devOffset, ByteBuffer src) throws ReadOnlyException, IOException, IllegalArgumentException {
            if (closed) throw new ClosedChannelException();
            if (devOffset > this.getSize())
                throw new IOException("IO address overflow: " + devOffset + " > " + this.getSize());
            this.parent.write(this.partitionEntry.start + devOffset, src);
        }

        @Override
        public void flush() throws IOException {
            this.parent.flush();
        }

        @Override
        public int getSectorSize() throws IOException {
            return this.parent.getSectorSize();
        }

        @Override
        public void close() {
            this.closed = true;
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public boolean isReadOnly() {
            return this.parent.isReadOnly() || this.partitionEntry.readOnlyStatus;
        }
    }
}
