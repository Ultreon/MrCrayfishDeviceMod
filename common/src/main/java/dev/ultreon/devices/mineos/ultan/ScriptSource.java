package dev.ultreon.devices.mineos.ultan;

import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@FunctionalInterface
public interface ScriptSource {
    String open() throws IOException;

    static ScriptSource direct(String script) {
        return () -> script;
    }

    static ScriptSource ofNbt(Tag tag) {
        if (tag instanceof StringTag) {
            return tag::getAsString;
        }

        return errored("Not a string tag!");
    }

    static ScriptSource read(File file) {
        return read(file.toPath());
    }

    static ScriptSource read(Path path) {
        return () -> Files.readString(path);
    }

    static ScriptSource errored(String message) {
        return () -> {
            throw new IOException(message);
        };
    }
}
