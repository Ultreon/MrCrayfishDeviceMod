package dev.ultreon.mineos.object;

import java.util.regex.Pattern;

public record ApplicationID(String namespace, String name) {
    public static final Pattern NAMESPACE = Pattern.compile("[a-zA-Z0-9_]{2,}(\\.[a-zA-Z0-9_]+)+");
    public static final Pattern NAME = Pattern.compile("[a-zA-Z0-9_]{3,}");

    public ApplicationID {
        this.validate(namespace, name);
    }

    private void validate(String namespace, String name) {
        if (!NAMESPACE.matcher(namespace).matches()) {
            throw new IllegalArgumentException("Invalid namespace: " + namespace);
        }
        if (!NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid name: " + name);
        }
    }
}
