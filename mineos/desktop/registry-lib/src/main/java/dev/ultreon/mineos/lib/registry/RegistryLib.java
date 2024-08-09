package dev.ultreon.mineos.lib.registry;

public class RegistryLib {
    private static RegistryLib instance = new RegistryLib();

    private RegistryLib() {

    }

    public static RegistryLib get() {
        return instance;
    }

    public RegistryHive getHive(String name) {

    }
}
