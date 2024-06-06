package dev.ultreon.devices.mineos.ultan;

public class CompileException extends Exception {
    public CompileException() {
        super();
    }

    public CompileException(String message) {
        super(message);
    }

    public CompileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompileException(Throwable cause) {
        super(cause);
    }
}
