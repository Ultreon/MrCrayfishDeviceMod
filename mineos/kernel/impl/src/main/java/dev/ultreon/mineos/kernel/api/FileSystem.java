package dev.ultreon.mineos.kernel.api;

import java.nio.file.Path;

public class FileSystem {
    private static FileSystem instance = new FileSystem();

    private FileSystem() {
        Kern.get().setFileSystem(this);
    }

    public FSEntry get(Path path) {
        return Kern.get().getFileInfo(path);
    }

    public static FileSystem get() {
        return instance;
    }
}
