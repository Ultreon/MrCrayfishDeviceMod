package dev.ultreon.devices.api;

import java.io.IOException;
import java.io.InputStream;

public interface TextureLoader {
    int load(InputStream stream) throws IOException;

    void destroy(int textureID);
}
