package dev.ultreon.devices.impl.app.listener;

/**
 * The initialization listener interface. Used for running
 * code when a layout is initialized.
 *
 * @author MrCrayfish
 */
public interface InitListener {
    /**
     * Called when a layout is set as the current layout.
     */
    void onInit();
}
