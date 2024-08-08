package dev.ultreon.devices.api.bios;

public interface InterruptData {
    <T> T getField(String name);

    void setField(String name, Object value);
}
