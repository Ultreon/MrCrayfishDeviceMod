package com.ultreon.devices.core.exceptions;

public abstract class MineOSException extends RuntimeException {
    public MineOSException() {
        super();
    }

    public MineOSException(String message) {
        super(message);
    }

    public MineOSException(String message, Throwable cause) {
        super(message, cause);
    }

    public MineOSException(Throwable cause) {
        super(cause);
    }
}
