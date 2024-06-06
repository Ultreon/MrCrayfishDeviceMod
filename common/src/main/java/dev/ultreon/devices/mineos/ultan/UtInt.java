package dev.ultreon.devices.mineos.ultan;

public class UtInt implements UtPrimitive {
    private Integer value;

    public UtInt(Integer value) {
        this.value = value;
    }

    @Override
    public Class<Integer> primitiveType() {
        return int.class;
    }

    @Override
    public Integer get() {
        return value;
    }

    @Override
    public void set(Object o) throws UltanException {
        if (!(o instanceof Integer)) throw new UltanException();
        this.value = (Integer) o;
    }

    @Override
    public UtValue copy() {
        return null;
    }
}
