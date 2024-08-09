package dev.ultreon.mineos.apps.gitweb.module;

import dev.ultreon.mineos.apps.gitweb.component.container.ContainerBox;
import dev.ultreon.mineos.apps.gitweb.component.container.LoomBox;

import java.util.Map;

public class LoomModule extends ContainerModule {
    @Override
    public String[] getRequiredData() {
        return new String[]{"slot-banner"};
    }

    @Override
    public String[] getOptionalData() {
        return new String[]{"slot-dye", "slot-pattern", "slot-result"};
    }

    @Override
    public int getHeight() {
        return LoomBox.HEIGHT;
    }

    @Override
    public ContainerBox createContainer(Map<String, String> data) {
        return new LoomBox(getItem(data, "slot-banner"), getItem(data, "slot-dye"), getItem(data, "slot-pattern"), getItem(data, "slot-result"));
    }
}
// 128, 72