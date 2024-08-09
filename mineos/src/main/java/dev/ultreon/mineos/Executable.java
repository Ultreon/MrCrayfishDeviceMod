package dev.ultreon.mineos;

import dev.ultreon.mineos.kernel.Kern;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

public class Executable {
    private final ZipFile zipFile;
    private final String mainClass;
    private final Kern kern;

    public Executable(Path path, Kern kern) throws IOException {
        this.zipFile = ZipFile.builder().setSeekableByteChannel(Kern.get().openChannel(path)).get();
        this.kern = kern;

        ZipArchiveEntry entry = zipFile.getEntry("META-INF/MANIFEST.MF");
        if (entry == null) throw new IllegalArgumentException("Invalid executable");
        try {
            byte[] manifestAttrs = zipFile.getInputStream(entry).readAllBytes();
            if (manifestAttrs.length == 0) throw new IllegalArgumentException("Invalid executable");
            String manifest = new String(manifestAttrs, StandardCharsets.UTF_8);
            if (!manifest.startsWith("Manifest-Version: 1.0")) throw new IllegalArgumentException("Invalid executable");

            Properties properties = new Properties();
            properties.load(new StringReader(manifest));

            String mainClass = properties.getProperty("Main-Class");
            if (mainClass == null) throw new IllegalArgumentException("Main entry point not found!");

            this.mainClass = mainClass;
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid executable", e);
        }
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

    public void run(AppClassLoader classLoader, String[] args) {
        try {
            Class<?> clazz = Class.forName(mainClass, true, classLoader);
            clazz.getMethod("main", String[].class).invoke(null, (Object) args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Kern getKern() {
        return kern;
    }
}
