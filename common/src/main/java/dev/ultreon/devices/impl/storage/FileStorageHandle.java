package dev.ultreon.devices.impl.storage;

import com.google.gson.Gson;
import dev.ultreon.devices.api.SystemException;
import dev.ultreon.devices.api.storage.FileHandle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FileStorageHandle implements FileHandle {
    private final String filePath;
    private final Path path;
    private final UUID player;

    public FileStorageHandle(String path, UUID player) {
        this.player = player;
        if (!path.startsWith("/")) throw new IllegalArgumentException("Path must be absolute");
        if (path.equals("/")) {
            this.filePath = "/";
            this.path = Path.of(".");
            return;
        }

        Path p = Path.of(path.substring(1));
        if (p.isAbsolute()) throw new IllegalArgumentException("Illegal path: " + path);

        this.filePath = path;
        this.path = p;
    }

    public Path toPath() {
        return path;
    }

    @Override
    public String path() {
        return filePath;
    }

    @Override
    public InputStream read() throws IOException {
        return Files.newInputStream(path, StandardOpenOption.READ);
    }

    @Override
    public OutputStream write() throws IOException {
        return Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    @Override
    public <T> T read(Class<T> clazz) throws IOException {
        return new Gson().fromJson(readString(), clazz);
    }

    @Override
    public <T> void write(T object) throws IOException {
        writeString(new Gson().toJson(object));
    }

    @Override
    public CompoundTag readNbt() throws IOException {
        return NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
    }

    @Override
    public void writeNbt(CompoundTag ubo) throws IOException {
        NbtIo.writeCompressed(ubo, path);
    }

    @Override
    public boolean isDirectory() {
        return Files.isDirectory(path);
    }

    @Override
    public boolean isFile() {
        return Files.isRegularFile(path);
    }

    @Override
    public boolean isSymbolicLink() {
        return Files.isSymbolicLink(path);
    }

    @Override
    public long length() {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean isReadable() {
        return Files.isReadable(path);
    }

    @Override
    public boolean isWritable() {
        return Files.isWritable(path);
    }

    @Override
    public boolean isExecutable() {
        return Files.isExecutable(path);
    }

    @Override
    public boolean exists() {
        return Files.exists(path);
    }

    @Override
    public FileHandle get(String path) {
        if (path.startsWith("/")) throw new IllegalArgumentException("Path must be relative");

        return Storages.file(this.player).get(this.filePath + path);
    }

    @Override
    public void createDirs() {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public List<FileHandle> listDir() {
        try (Stream<Path> list = Files.list(path)) {
            return list.map(path1 -> Storages.file(this.player).get(this.filePath + path1.getFileName().toString())).toList();
        } catch (IOException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String fileName() {
        return path.getFileName().toString();
    }
}
