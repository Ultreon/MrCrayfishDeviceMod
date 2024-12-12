package com.ultreon.devices.exception;

import com.ultreon.devices.core.ComputerScreen;

/// ## Exception for when a world is not loaded
/// This exception occurs whn the world isn't loaded in a world-required environment.
///
/// @author [XyperCode](https://github.com/XyperCode)
/// @see ComputerScreen#isWorldLess()
public class WorldLessException extends Exception {
    public WorldLessException(String message) {
        super(message);
    }
}
