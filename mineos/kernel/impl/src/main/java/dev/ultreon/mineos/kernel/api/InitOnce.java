package dev.ultreon.mineos.kernel.api;

final class InitOnce<Value> {
    private volatile boolean initialized = false;

    private Value value;

    public Value get() {
        if (!initialized) {
            throw new UnintializedValueException();
        }
        return value;
    }

    public void init(Value value) {
        if (!initialized) {
            initialized = true;
        }

        this.value = value;
    }
}
