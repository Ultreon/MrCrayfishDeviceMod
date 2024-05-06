package dev.ultreon.devices.mineos.apps.gitweb.module;

import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.mineos.apps.gitweb.layout.ModuleLayout;
import dev.ultreon.devices.mineos.apps.gitweb.component.GitWebFrame;

import java.util.Map;

/**
 * @author MrCrayfish
 */
public abstract class Module {

    public abstract String[] getRequiredData();

    public abstract String[] getOptionalData();

    public abstract int calculateHeight(Map<String, String> data, int width);

    public abstract void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data);

    public void tick(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {

    }

    public void modify(GitWebFrame frame, ModuleLayout layout, int width, Map<String, String> data){}

    //TODO: slideshow module, text area syntax highlighting
}
