package com.ultreon.devices.gui.config;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.MutableComponent;

public abstract class ValueSliderButton extends AbstractSliderButton {
    private final double min;
    private final double max;

    public ValueSliderButton(int x, int y, int width, int height, MutableComponent component, double v, double min, double max) {
        super(x, y, width, height, component, (v - min) / (max - min));
        this.min = min;
        this.max = max;
    }

    @Override
    protected abstract void updateMessage();

    @Override
    protected void applyValue() {

    }

    public double getValue() {
        return this.min + this.value * (this.max - this.min);
    }
}
