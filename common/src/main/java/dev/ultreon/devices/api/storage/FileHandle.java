package dev.ultreon.devices.api.storage;

import dev.ultreon.devices.UltreonDevicesMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.util.List;

public interface FileHandle {
    default String readString() throws IOException {
        try (InputStream read = read()) {
            return new String(read.readAllBytes());
        }
    }

    default byte[] readBytes() throws IOException {
        try (InputStream read = read()) {
            return read.readAllBytes();
        }
    }

    default void writeString(String string) throws IOException {
        try (OutputStream write = write()) {
            write.write(string.getBytes());
        }
    }

    default void writeBytes(byte[] bytes) throws IOException {
        try (OutputStream write = write()) {
            write.write(bytes);
        }
    }

    String path();

    InputStream read() throws IOException;

    OutputStream write() throws IOException;

    default <T> T read(Class<T> clazz) throws IOException {
        return UltreonDevicesMod.GSON.fromJson(reader(), clazz);
    }

    default <T> void write(T object) throws IOException {
        write(UltreonDevicesMod.GSON.toJson(object));
    }

    default CompoundTag readNbt() throws IOException {
        return NbtIo.read(dataReader(), NbtAccounter.unlimitedHeap());
    }

    default void writeNbt(CompoundTag ubo) throws IOException {
        NbtIo.write(ubo, dataWriter());
    }

    default DataInput dataReader() throws IOException {
        return new DataInputStream(read());
    }

    default DataOutput dataWriter() throws IOException {
        return new DataOutputStream(write());
    }

    boolean isDirectory();

    boolean isFile();

    boolean isSymbolicLink();

    long length() throws IOException;

    default boolean isReadable() {
        return false;
    }

    default boolean isWritable() {
        return false;
    }

    default Reader reader() throws IOException {
        return new InputStreamReader(read());
    }

    default Writer writer() throws IOException {
        return new OutputStreamWriter(write());
    }

    static FileHandle resource(ResourceLocation path) {
        return DataStorage.findDataStorage("resource-path").get(path.toString());
    }

    static FileHandle storage(String path) {
        return DataStorage.findDataStorage("storage").get(path);
    }

    static FileHandle uri(String uri) {
        return DataStorage.findDataStorage("uri").get(uri);
    }

    default boolean isExecutable() {
        return false;
    }

    boolean exists();

    default boolean isAccessible() {
        return exists();
    }

    FileHandle get(String path);

    default CompoundTag readNbt(String path) throws IOException {
        return get(path).readNbt();
    }

    default void writeNbt(String path, CompoundTag ubo) throws IOException {
        get(path).writeNbt(ubo);
    }

    void createDirs();

    List<FileHandle> listDir();

    String fileName();

    default String extension() {
        return fileName().substring(fileName().lastIndexOf('.') + 1);
    }
}
