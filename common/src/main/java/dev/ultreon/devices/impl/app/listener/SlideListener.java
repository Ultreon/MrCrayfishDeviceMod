package dev.ultreon.devices.impl.app.listener;

import dev.ultreon.devices.impl.app.component.Slider;

/**
 * The slider listener interface. Used for getting
 * the percentage value on a {@link Slider} every
 * time it is moved.
 *
 * @author MrCrayfish
 */
public interface SlideListener {
    /**
     * Called when a sliders position has moved
     *
     * @param percentage the percentage from the left
     */
    void onSlide(float percentage);
}
