package dev.ultreon.devices.mineos.ultan;

public class UltanRuntimeException extends RuntimeException {
    public UltanRuntimeException() {
        super();
    }

    public UltanRuntimeException(String message) {
        super(message);
    }

    public UltanRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UltanRuntimeException(Throwable cause) {
        super(cause);
    }
}
