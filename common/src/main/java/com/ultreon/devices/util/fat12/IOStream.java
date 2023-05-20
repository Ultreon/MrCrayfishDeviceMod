package com.ultreon.devices.util.fat12;

import de.waldheinz.fs.fat.FatFile;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class IOStream implements Closeable {
    public static final byte NONE = 0b00;
    public static final byte READ = 0b01;
    public static final byte WRITE = 0b10;

    private final FatFile file;
    private final Input in;
    private final Output out;
    private final Closeable closeable;
    private long off = 0;

    public IOStream(FatFile file, boolean append, byte mode, Closeable closeable) throws IOException {
        this.file = file;

        if (mode == NONE) throw new IOException("Invalid mode, must be read, write or both.");
        this.in = (mode & READ) != 0 ? new Input() : null;
        this.out = (mode & WRITE) != 0 ? new Output() : null;
        this.closeable = closeable;
    }

    public FatFile getFile() {
        return file;
    }

    public long seek(long off) {
        if (off < 0) throw new IllegalArgumentException("Negative offset is not supported");

        this.off = this.off > size() ? size() : off;
        return this.off;
    }

    public long peek() {
        return this.off;
    }

    public void write(int b) throws IOException {
        if (out == null) throw new IOException("Read-only file stream");

        ByteBuffer buf = ByteBuffer.wrap(new byte[]{(byte) b});
        this.file.write(this.off, buf);
        buf.clear();
        this.off++;
    }

    public int read() throws IOException {
        if (out == null) throw new IOException("Write-only file stream");

        ByteBuffer buf = ByteBuffer.allocate(1);
        if (this.off == size()) {
            return -1;
        }
        this.file.read(this.off, buf);
        this.off++;
        byte b = buf.get(0);
        buf.clear();
        return b;
    }

    public long size() {
        return file.getLength();
    }

    public boolean isReadOnly() {
        return in != null && out == null;
    }

    public boolean isWriteOnly() {
        return in == null && out != null;
    }

    public boolean isReadWrite() {
        return in != null && out != null;
    }

    public boolean isUnusable() {
        return in == null && out == null;
    }

    public Input getIn() {
        return in;
    }

    public Output getOut() {
        return out;
    }

    public void flush() {
        this.file.flush();
    }

    @Override
    public void close() throws IOException {
        this.closeable.close();
    }

    public class Input extends InputStream {
        @Override
        public int read() throws IOException {
            return IOStream.this.read();
        }

        @Override
        public int available() {
            return off + 1 < size() ? 1 : 0;
        }
    }

    public class Output extends OutputStream {
        @Override
        public void write(int b) throws IOException {
            IOStream.this.write(b);
        }

        @Override
        public void flush() {
            IOStream.this.flush();
        }
    }
}
