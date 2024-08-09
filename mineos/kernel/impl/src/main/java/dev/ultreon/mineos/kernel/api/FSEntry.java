package dev.ultreon.mineos.kernel.api;

import java.nio.channels.SeekableByteChannel;

public record FSEntry(String path) {
    public SeekableByteChannel openChannel() {
        return Kern.get().open(path);
    }
}
