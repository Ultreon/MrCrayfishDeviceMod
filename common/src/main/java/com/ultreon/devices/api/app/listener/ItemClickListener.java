package com.ultreon.devices.api.app.listener;

/**
 * The click listener interface. Used for handling clicks
 * on components.
 *
 * @author MrCrayfish
 */
public interface ItemClickListener<E> {
    /**
     * Called when component is clicked
     *
     * @param e           the component that was clicked
     * @param mouseX
     * @param mouseY
     * @param mouseButton the mouse button used to click
     */
    void onClick(E e, int index, int mouseX, int mouseY, int mouseButton);
}
