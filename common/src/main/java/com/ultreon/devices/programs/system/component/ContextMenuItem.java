package com.ultreon.devices.programs.system.component;

import com.ultreon.devices.api.app.renderer.ListItemRenderer;
import com.ultreon.devices.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.Objects;

/**
 * Represents a context menu item.
 *
 * @author XyperCode
 */
public final class ContextMenuItem<T> extends ListItemRenderer<ContextMenuItem<T>> {
    private final Component name;
    private final EventHandler<T> action;

    /**
     * @param name   The name of the context menu item
     * @param action The action to execute
     */
    public ContextMenuItem(Component name, EventHandler<T> action) {
        super(20);
        this.name = name;
        this.action = action;
    }

    /**
     * Executes the context menu action.
     */
    public void execute(T eventInfo) {
        action.run(eventInfo);
    }

    public Component name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ContextMenuItem) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, action);
    }

    @Override
    public String toString() {
        return "ContextMenuItem[" +
                "name=" + name + ", " +
                "action=" + action + ']';
    }

    @Override
    public void render(GuiGraphics graphics, ContextMenuItem contextMenuItem, Minecraft mc, int x, int y, int width, int height, boolean selected) {
        graphics.fill(x, y, x + width, y + height, selected ? Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor() : new Color(0, 0, 0, 0).getRGB());

        graphics.drawString(Minecraft.getInstance().font, contextMenuItem.name(), x + 5, y + 5, Laptop.getSystem().getSettings().getColorScheme().getTextColor());
    }

    @FunctionalInterface
    public interface EventHandler<T> {
        void run(T eventInfo);
    }
}
