package dev.ultreon.devices.impl.bios;

import com.google.common.collect.Iterators;
import de.waldheinz.fs.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class VirtualDisk extends Disk implements FileSystem {
    private final Path bootFile;
    private FsDirectory root;

    public VirtualDisk(Path bootFile) {
        this.bootFile = bootFile;

        this.root = new VirtualDiskRoot(bootFile);
    }

    @Override
    public FileSystem open() {
        return this;
    }

    @Override
    public FsDirectory getRoot() {
        return root;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public long getTotalSpace() {
        return 0;
    }

    @Override
    public long getFreeSpace() {
        return 0;
    }

    @Override
    public long getUsableSpace() {
        return 0;
    }

    @Override
    public void flush() {

    }

    private class VirtualDiskRoot implements FsDirectory {
        private final VirtualDirectory bootDir;

        public VirtualDiskRoot(Path bootFile) {
            this.bootDir = new VirtualDirectory("boot");
            VirtualDirectory vefiDir = new VirtualDirectory("vefi", List.of(
                    new VirtualFile(bootFile, "boot.jefi")
            ));
            this.bootDir.entries.put("vefi", vefiDir);
        }

        @Override
        public Iterator<FsDirectoryEntry> iterator() {
            return Iterators.singletonIterator(bootDir);
        }

        @Override
        public FsDirectoryEntry getEntry(String name) throws IOException {
            if (name.equals("boot")) {
                return bootDir;
            } else {
                throw new FileNotFoundException(name);
            }
        }

        @Override
        public FsDirectoryEntry addFile(String name) {
            throw new ReadOnlyException();
        }

        @Override
        public FsDirectoryEntry addDirectory(String name) {
            throw new ReadOnlyException();
        }

        @Override
        public void remove(String name) {
            throw new ReadOnlyException();
        }

        @Override
        public void flush() {
            // No
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }
    }

    private static class VirtualDirectory implements FsDirectory, FsDirectoryEntry {
        final Map<String, FsDirectoryEntry> entries = new LinkedHashMap<>();
        private final String name;

        public VirtualDirectory(String name) {

            this.name = name;
        }

        public VirtualDirectory(String name, List<FsDirectoryEntry> entries) {
            this.name = name;
            this.entries.putAll(entries.stream().collect(Collectors.toMap(FsDirectoryEntry::getName, e -> e)));
        }

        @Override
        public @NotNull Iterator<FsDirectoryEntry> iterator() {
            return entries.values().iterator();
        }

        @Override
        public FsDirectoryEntry getEntry(String name) {
            return entries.get(name);
        }

        @Override
        public FsDirectoryEntry addFile(String name) {
            throw new ReadOnlyException();
        }

        @Override
        public FsDirectoryEntry addDirectory(String name) {
            throw new ReadOnlyException();
        }

        @Override
        public void remove(String name) {
            throw new ReadOnlyException();
        }

        @Override
        public void flush() {
            // No
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public FsDirectory getParent() {
            return null;
        }

        @Override
        public long getLastModified() {
            return 0;
        }

        @Override
        public long getCreated() {
            return 0;
        }

        @Override
        public long getLastAccessed() {
            return 0;
        }

        @Override
        public boolean isFile() {
            return false;
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public void setName(String newName) {

        }

        @Override
        public void setLastModified(long lastModified) {

        }

        @Override
        public FsFile getFile() {
            return null;
        }

        @Override
        public FsDirectory getDirectory() {
            return null;
        }

        @Override
        public boolean isDirty() {
            return false;
        }
    }

    private static class VirtualFile implements FsFile, FsDirectoryEntry {
        private final Path hostFile;
        private final String name;
        FsDirectory parent;

        public VirtualFile(Path hostFile, String name) {
            this.hostFile = hostFile;
            this.name = name;
        }

        @Override
        public long getLength() {
            try {
                return Files.size(hostFile);
            } catch (IOException e) {
                return 0;
            }
        }

        @Override
        public void setLength(long length) {
            throw new ReadOnlyException();
        }

        @Override
        public void read(long offset, ByteBuffer dest) throws IOException {
            try (var channel = Files.newByteChannel(hostFile)) {
                channel.position(offset);
                channel.read(dest);
            }
        }

        @Override
        public void write(long offset, ByteBuffer src) throws ReadOnlyException {
            throw new ReadOnlyException();
        }

        @Override
        public void flush() {
            throw new ReadOnlyException();
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public FsDirectory getParent() {
            return parent;
        }

        @Override
        public long getLastModified() {
            return 0;
        }

        @Override
        public long getCreated() {
            return 0;
        }

        @Override
        public long getLastAccessed() {
            return 0;
        }

        @Override
        public boolean isFile() {
            return false;
        }

        @Override
        public boolean isDirectory() {
            return false;
        }

        @Override
        public void setName(String newName) {

        }

        @Override
        public void setLastModified(long lastModified) {

        }

        @Override
        public FsFile getFile() {
            return this;
        }

        @Override
        public FsDirectory getDirectory() throws IOException {
            throw new IOException("File is not a directory");
        }

        @Override
        public boolean isDirty() {
            return false;
        }

    }
}
