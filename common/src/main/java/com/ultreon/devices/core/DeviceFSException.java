package com.ultreon.devices.core;

import java.io.IOException;

public class DeviceFSException extends RuntimeException {
    public DeviceFSException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeviceFSException(String message) {
        super(message);
    }

    public DeviceFSException(Throwable cause) {
        super(cause);
    }
}
