package com.ultreon.devices.programs.gitweb.module;

import com.ultreon.devices.Devices;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.gitweb.GitWebApp;
import com.ultreon.devices.programs.gitweb.component.GitWebFrame;
import com.ultreon.devices.programs.system.AppStore;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class AppLinkModule extends Module {
    @Override
    public String[] getRequiredData() {
        return new String[]{"app"};
    }

    @Override
    public String[] getOptionalData() {
        return new String[]{"text"};
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width) {
        return 45;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
        int height = calculateHeight(data, width) - 5;
        AppInfo info = ApplicationManager.getApplication(ResourceLocation.tryParse(data.get("app")));

        int section = layout.width / 6;
        Button button = new Button(0, 10, "Install", Icons.IMPORT);
        button.left = section * 5 - 70 - 5;
        button.setSize(70, height - 15);
        button.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (frame.getApp() instanceof GitWebApp gitWeb) {
                gitWeb.getSystem().ifPresent(a -> {
                    if (info != null) {
                        Devices.LOGGER.debug(GitWebApp.MARKER, "Opening app page in application market for: " + info.getId());
                    } else {
                        Devices.LOGGER.warn(GitWebApp.MARKER, "Trying to open app page in application market for a 'null' app.");
                    }
                    var b = a.openApplication(ApplicationManager.getApplication(ResourceLocation.tryParse("devices:app_store")));
                    if (b instanceof AppStore store) {
                        store.queueOpen(info);
                    }
                });
            }
        });
        layout.addComponent(button);
    }
}
