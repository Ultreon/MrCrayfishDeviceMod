package dev.ultreon.devices.mineos.ultan;

public class UltanException extends Exception {
    public UltanException() {
        super();
    }

    public UltanException(String message) {
        super(message);
    }

    public UltanException(String message, Throwable cause) {
        super(message, cause);
    }

    public UltanException(Throwable cause) {
        super(cause);
    }
}
