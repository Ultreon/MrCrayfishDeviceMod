package dev.ultreon.vbios;

public interface InterruptData {
    <T> T getField(String name);

    void setField(String name, Object value);

    BiosInterruptType interruptType();
}
