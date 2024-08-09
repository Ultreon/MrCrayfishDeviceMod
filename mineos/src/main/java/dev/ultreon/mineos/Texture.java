package dev.ultreon.mineos;

import dev.ultreon.devices.api.TextureLoader;

import java.io.InputStream;

public class Texture {
    private final InputStream stream;
    private int id;

    public Texture(InputStream stream) {
        this.stream = stream;
    }

    public void load(TextureLoader loader) {
        this.id = loader.load(stream);
    }

    public int getId() {
        return id;
    }

    public void dispose(TextureLoader loader) {
        loader.destroy(id);
    }
}
