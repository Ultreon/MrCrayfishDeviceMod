package dev.ultreon.devices.impl.bios;

import dev.ultreon.vbios.BiosInterruptType;
import dev.ultreon.vbios.InterruptData;

import java.lang.reflect.Field;

public abstract class AbstractInterruptData implements InterruptData {
    private final VBios vbios;
    private final BiosInterruptType interrupt;

    public AbstractInterruptData(VBios vbios, BiosInterruptType interrupt) {
        this.vbios = vbios;
        this.interrupt = interrupt;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getField(String name) {
        Class<? extends AbstractInterruptData> self = this.getClass();
        try {
            Field declaredField = self.getDeclaredField(name);
            declaredField.setAccessible(true);
            return (T) declaredField.get(this); // Oooo spooky
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    @Override
    public void setField(String name, Object value) {
        Class<? extends AbstractInterruptData> self = this.getClass();
        try {
            Field declaredField = self.getDeclaredField(name);
            declaredField.setAccessible(true);
            declaredField.set(this, value);
        } catch (NoSuchFieldException e) {
            return;
        } catch (IllegalAccessException e) {
            vbios.fault(e);
        }
    }

    @Override
    public BiosInterruptType interruptType() {
        return interrupt;
    }
}
