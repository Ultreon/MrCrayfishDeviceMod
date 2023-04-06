package com.ultreon.devices.object;

import com.ultreon.devices.api.app.IIcon;
import com.ultreon.devices.api.app.listener.ClickListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * @author MrCrayfish
 */
public class TrayItem {
    private IIcon icon;
    private ClickListener listener;

    public TrayItem(IIcon icon) {
        this.icon = icon;
    }

    public void init() {
    }

    public void tick() {
    }

    @Environment(EnvType.CLIENT)
    public void setIcon(IIcon icon) {
        this.icon = icon;
    }

    @Environment(EnvType.CLIENT)
    public IIcon getIcon() {
        return icon;
    }

    public void setClickListener(ClickListener listener) {
        this.listener = listener;
    }

    public void handleClick(int mouseX, int mouseY, int mouseButton) {
        if (listener != null) {
            listener.onClick(mouseX, mouseY, mouseButton);
        }
    }
}
