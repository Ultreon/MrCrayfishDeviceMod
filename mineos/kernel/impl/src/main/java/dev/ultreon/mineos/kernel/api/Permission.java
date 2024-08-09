package dev.ultreon.mineos.kernel.api;

public class Permission {
    private final String name;

    public Permission(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }
}
