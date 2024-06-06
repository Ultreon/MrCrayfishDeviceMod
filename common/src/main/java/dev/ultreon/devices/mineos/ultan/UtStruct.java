package dev.ultreon.devices.mineos.ultan;

import java.util.HashSet;
import java.util.Set;

public class UtStruct implements UtValue {
    private final Entry[] entries;

    public UtStruct(String[] names, UtValue[] value) {
        entries = new Entry[names.length];

        Set<String> seen = new HashSet<>();
        for (int i = 0; i < entries.length; i++) {
            if (seen.contains(names[i])) throw new UltanRuntimeException("Invalid struct");
            seen.add(names[i]);
            entries[i] = new Entry(names[i], value[i]);
        }
    }

    @Override
    public UtStruct get() {
        return this;
    }

    public UtValue read(String name) {
        for (Entry e : entries) {
            e.name == name;
        }
    }

    @Override
    public void set(Object o) throws UltanException {
        if (!(o instanceof UtStruct)) {
            throw new UltanException("Invalid cast!");
        }
    }

    @Override
    public UtStruct copy() {
        return new UtStruct();
    }

    public static class Entry {
        public final String name;
        public final UtValue value;

        public Entry(String name, UtValue value) {
            this.name = name;
            this.value = value;
        }
    }
}
