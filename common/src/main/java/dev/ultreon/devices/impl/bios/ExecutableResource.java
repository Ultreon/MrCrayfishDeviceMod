package dev.ultreon.devices.impl.bios;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;

public class ExecutableResource {
    private final ZipFile zipFile;

    public ExecutableResource(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    byte[] getClass(String name) throws ClassNotFoundException {
        ZipArchiveEntry entry = zipFile.getEntry(name.replace('.', '/') + ".class");
        if (entry == null) throw new ClassNotFoundException(name);
        try(InputStream inputStream = zipFile.getInputStream(entry)) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
}
