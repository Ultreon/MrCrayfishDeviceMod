package dev.ultreon.vbios;

@FunctionalInterface
public interface InterruptHandler {
    void onInterrupt(InterruptData interrupt);
}
