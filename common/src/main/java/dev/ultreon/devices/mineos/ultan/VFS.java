package dev.ultreon.devices.mineos.ultan;

public interface VFS {
    /**
     *
     *
     * @param path the path to the script file.
     * @return the script source.
     */
    String read(String path);
}
