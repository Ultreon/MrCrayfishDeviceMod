package dev.ultreon.mineos.kernel.api;

import reactor.core.publisher.Mono;

import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;

public interface Kern {
    InitOnce<Kern> INSTANCE = new InitOnce<>();

    static Kern get() {
        return INSTANCE.get();
    }

    void requestShutdown() throws SecurityException;

    void requestReboot() throws SecurityException;

    void requestHalt() throws SecurityException;

    void askPermission(Permission permission, Mono<PermissionTicket> callback) throws SecurityException;

    SeekableByteChannel openChannel(Path path);

    void setFileSystem(FileSystem fileSystem);

    FSEntry getFileInfo(Path path);

    SeekableByteChannel open(String path);

    void close(SeekableByteChannel channel);
}
