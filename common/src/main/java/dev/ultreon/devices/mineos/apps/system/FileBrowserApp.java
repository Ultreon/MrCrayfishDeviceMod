package dev.ultreon.devices.mineos.apps.system;


import dev.ultreon.devices.UltreonDevicesMod;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.api.app.Icons;
import dev.ultreon.devices.mineos.apps.system.component.FileBrowser;
import dev.ultreon.devices.mineos.client.MineOS;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.object.TrayItem;
import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.Nullable;

public class FileBrowserApp extends SystemApp {
    private FileBrowser browser;

    public FileBrowserApp() {
        this.setDefaultWidth(211);
        this.setDefaultHeight(145);
    }

    @Override
    public void init(@Nullable CompoundTag intent) {
        browser = new FileBrowser(0, 0, this, FileBrowser.Mode.FULL);
        browser.openFolder(FileSystem.DIR_HOME);
        this.addComponent(browser);
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }

    public static class FileBrowserTrayItem extends TrayItem {
        public FileBrowserTrayItem() {
            super(Icons.FOLDER, UltreonDevicesMod.id("file_browser"));
        }

        @Override
        public void handleClick(int mouseX, int mouseY, int mouseButton) {
            AppInfo info = ApplicationManager.getApplication(UltreonDevicesMod.id("file_browser"));
            if (info != null) {
                MineOS.getOpened().openApplication(info);
            }
        }
    }
}
