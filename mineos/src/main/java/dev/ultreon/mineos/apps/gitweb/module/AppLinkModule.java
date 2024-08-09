package dev.ultreon.mineos.apps.gitweb.module;

import dev.ultreon.devices.impl.ApplicationManager;
import dev.ultreon.devices.impl.app.Icons;
import dev.ultreon.devices.impl.app.Layout;
import dev.ultreon.devices.impl.app.component.Button;
import dev.ultreon.mineos.apps.gitweb.GitWebApp;
import dev.ultreon.mineos.apps.system.AppStore;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.mineos.object.AppInfo;
import dev.ultreon.mineos.apps.gitweb.component.GitWebFrame;
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
                DebugLog.log("FRAME");
                gitWeb.getSystem().ifPresent(a -> {
                    DebugLog.log("OPENING APP");
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
