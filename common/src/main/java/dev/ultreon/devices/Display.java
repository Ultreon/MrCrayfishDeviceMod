package dev.ultreon.devices;

import dev.ultreon.devices.api.os.OperatingSystem;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.client.DisplayGui;
import dev.ultreon.devices.mineos.apps.system.DisplayResolution;

import java.util.function.Consumer;

public interface Display {
    void setResolution(DisplayResolution resolution);

    boolean isPresent();

    boolean isConnected();

    int getScreenWidth();

    int getScreenHeight();

    int getMouseX();

    int getMouseY();

    float getPartialTicks();

    int getMaxWidth();

    int getMaxHeight();

    void renderBezels();

    ComputerBlockEntity getComputer();

    OperatingSystem getOS();
}
