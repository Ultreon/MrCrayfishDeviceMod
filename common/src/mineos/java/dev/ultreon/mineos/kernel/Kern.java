package dev.ultreon.mineos.kernel;

import dev.ultreon.mineos.impl.Permission;

import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;

public interface Kern {
    static Kern get() {

    }

    void requestShutdown() throws SecurityException;

    void requestReboot() throws SecurityException;

    void requestHalt() throws SecurityException;

    void askPermission(Permission permission, Mono<PermissionCallback> callback) throws SecurityException;

    SeekableByteChannel openChannel(Path path);
}
