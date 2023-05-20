package com.ultreon.devices.util.fat12;

import de.waldheinz.fs.BlockDevice;
import de.waldheinz.fs.FsDirectoryEntry;
import de.waldheinz.fs.fat.FatFile;
import de.waldheinz.fs.fat.FatFileSystem;
import de.waldheinz.fs.fat.FatLfnDirectory;
import de.waldheinz.fs.fat.FatLfnDirectoryEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FatApi {
    private final BlockDevice blockDevice;
    private final FatFileSystem fs;

    public FatApi(BlockDevice blockDevice, FatFileSystem fs) {
        this.blockDevice = blockDevice;
        this.fs = fs;
    }

    private LocatedEntry getLocatedEntry(String path, String[] entries) throws IOException {
        FatLfnDirectoryEntry current = null;
        FatLfnDirectory parent = null;

        for (int i = 0, entriesLength = entries.length; i < entriesLength; i++) {
            var e = entries[i];
            switch (i) {
                case 0:
                    break;
                case 1:
                    if (e.isEmpty() || e.isBlank()) throw new IllegalArgumentException("Invalid filepath: " + path);

                    parent = fs.getRoot();
                    current = fs.getRoot().getEntry(e);
                    break;
                default:
                    if (e.isEmpty() || e.isBlank()) throw new IllegalArgumentException("Invalid filepath: " + path);

                    if (current == null) throw new FileNotFoundException(path);
                    if (current.isFile()) throw new FileNotFoundException(path);

                    parent = current.getDirectory();
                    current = current.getDirectory().getEntry(e);
                    break;
            }
        }

        if (parent == null) throw new Error("Requested parent directory entry is null");

        return new LocatedEntry(parent, current);
    }

    private FatLfnDirectory getDirectory(String path, String[] entries) throws IOException {
        LocatedEntry locatedEntry = getLocatedEntry(path, entries);
        FatLfnDirectoryEntry current = locatedEntry.current();

        if (!current.isDirectory()) throw new IOException("Requested path is not a directory");

        return current.getDirectory();
    }

    private FatFile getFile(String path, boolean create, String[] entries) throws IOException {
        LocatedEntry locatedEntry = getLocatedEntry(path, entries);
        FatLfnDirectoryEntry current = locatedEntry.current();
        FatLfnDirectory parent = locatedEntry.parent();

        FatFile file;

        if (create) {
            if (current != null) throw new FileAlreadyExistsException(path);

            file = parent.addFile(entries[entries.length - 1]).getFile();
        } else {
            if (!current.isFile()) throw new IOException("Requested path is not a file");

            file = current.getFile();
        }
        return file;
    }

    public IOStream openFile(String path, OpenOption... openOption) throws IOException {
        List<OpenOption> options = List.of(openOption);

        if (!path.startsWith("/")) throw new IllegalArgumentException("Invalid filepath: " + path);

        String[] entries = path.split("/");

        LocatedEntry locatedEntry = getLocatedEntry(path, entries);
        FatLfnDirectoryEntry current = locatedEntry.current();
        FatLfnDirectory parent = locatedEntry.parent();

        FatFile file;

        if (options.contains(StandardOpenOption.CREATE_NEW)) {
            if (current != null) throw new FileAlreadyExistsException(path);

            file = parent.addFile(entries[entries.length - 1]).getFile();
        } else if (current == null && options.contains(StandardOpenOption.CREATE)) {
            file = parent.addFile(entries[entries.length - 1]).getFile();
        } else if (current == null) {
            throw new FileNotFoundException(path);
        } else if (!current.isFile()) {
            throw new IOException("Requested path is not a file");
        } else {
            file = current.getFile();
        }

        if (!file.isValid()) throw new FileNotFoundException(path);

        byte mode = options.contains(StandardOpenOption.READ) ? IOStream.READ : IOStream.NONE;
        boolean append = options.contains(StandardOpenOption.APPEND);

        if (options.contains(StandardOpenOption.WRITE)) {
            if (file.isReadOnly()) throw new IOException("File is read-only: " + path);

            mode |= IOStream.WRITE;

            if (options.contains(StandardOpenOption.TRUNCATE_EXISTING)) file.setLength(0L);
        }

        return new IOStream(file, append, mode, () -> {
            file.flush();
            if (options.contains(StandardOpenOption.DELETE_ON_CLOSE)) {
                parent.remove(entries[entries.length - 1]);
            }
        });
    }

    public void createFile(String path, long length) throws IOException {
        if (!path.startsWith("/")) throw new IllegalArgumentException("Invalid filepath: " + path);

        String[] entries = path.split("/");

        LocatedEntry locatedEntry = getLocatedEntry(path, entries);
        FatLfnDirectoryEntry current = locatedEntry.current();
        FatLfnDirectory parent = locatedEntry.parent();

        if (current != null) throw new FileAlreadyExistsException(path);

        FatFile file = parent.addFile(entries[entries.length - 1]).getFile();
        file.setLength(length);
        file.flush();
    }

    public List<String> listDir(String path) throws IOException {
        if (!path.startsWith("/")) throw new IllegalArgumentException("Invalid filepath: " + path);

        String[] entries = path.split("/");

        FatLfnDirectory dir = getDirectory(path, entries);
        var list = new ArrayList<String>();

        for (var e : dir) {
            list.add(e.getName());
        }

        return list;
    }

    public List<String> listDir(String path, Predicate<FsDirectoryEntry> filter) throws IOException {
        if (!path.startsWith("/")) throw new IllegalArgumentException("Invalid filepath: " + path);

        String[] entries = path.split("/");

        FatLfnDirectory dir = getDirectory(path, entries);
        var list = new ArrayList<String>();

        for (var e : dir) {
            if (filter.test(e)) {
                list.add(e.getName());
            }
        }

        return list;
    }

    public List<String> listDirs(String path) throws IOException {
        if (!path.startsWith("/")) throw new IllegalArgumentException("Invalid filepath: " + path);

        String[] entries = path.split("/");

        FatLfnDirectory dir = getDirectory(path, entries);
        var list = new ArrayList<String>();

        for (var e : dir) {
            if (!e.isDirectory()) continue;
            list.add(e.getName());
        }

        return list;
    }

    public List<String> listFiles(String path) throws IOException {
        if (!path.startsWith("/")) throw new IllegalArgumentException("Invalid filepath: " + path);

        String[] entries = path.split("/");

        FatLfnDirectory dir = getDirectory(path, entries);
        var list = new ArrayList<String>();

        for (var e : dir) {
            if (!e.isFile()) continue;
            list.add(e.getName());
        }

        return list;
    }

    public BlockDevice getBlockDevice() {
        return blockDevice;
    }
}
