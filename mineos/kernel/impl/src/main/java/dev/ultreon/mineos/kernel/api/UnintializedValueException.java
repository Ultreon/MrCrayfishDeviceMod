package dev.ultreon.mineos.kernel.api;

public class UnintializedValueException extends RuntimeException {
    public UnintializedValueException() {
        super("Value uninitialized");
    }

    public UnintializedValueException(String message) {
        super(message);
    }
}
