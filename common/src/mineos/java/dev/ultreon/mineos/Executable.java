package dev.ultreon.mineos;

import dev.ultreon.mineos.kernel.Kern;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.IOException;
import java.nio.file.Path;

public class Executable {
    private final ZipFile zipFile;

    public Executable(Path path) throws IOException {
        this.zipFile = ZipFile.builder().setSeekableByteChannel(Kern.get().openChannel(path)).get();
    }

    public byte[] getClass(String name) throws ClassNotFoundException {
        ZipArchiveEntry entry = zipFile.getEntry(name.replace('.', '/') + ".class");
        if (entry == null) return null;
        try {
            return zipFile.getInputStream(entry).readAllBytes();
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
    }
}
