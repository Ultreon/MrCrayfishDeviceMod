package com.ultreon.devices.impl.storage;

import net.minecraft.resources.ResourceLocation;
import com.ultreon.devices.api.storage.FileHandle;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.ReadOnlyFileSystemException;
import java.util.List;

public class ResourceFileHandle implements FileHandle {
    private final ResourceLocation path;

    public ResourceFileHandle(ResourceLocation path) {
        this.path = path;
    }

    @Override
    public String path() {
        return path.toString();
    }

    @Override
    public InputStream read() throws IOException {
        return Minecraft.getInstance().getResourceManager().open(toResourceLocation());
    }

    @Override
    public OutputStream write() {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean isSymbolicLink() {
        return false;
    }

    @Override
    public long length() throws IOException {
        try (InputStream read = read()) {
            return read.available();
        }
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public boolean exists() {
        return Minecraft.getInstance().getResourceManager().getResource(toResourceLocation()).isPresent();
    }

    @Override
    public FileHandle get(String path) {
        return new ResourceFileHandle(new ResourceLocation(this.path.getNamespace(), this.path.getPath() + "/" + path));
    }

    @Override
    public void createDirs() {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public List<FileHandle> listDir() {
        return List.of();
    }

    @Override
    public String fileName() {
        String[] split = path.getPath().split("/");
        return split[split.length - 1];
    }

    public ResourceLocation getResourcePath() {
        return path;
    }

    public ResourceLocation toResourceLocation() {
        return new ResourceLocation(path.getNamespace(), path.getPath());
    }
}
