package dev.ultreon.devices;

import dev.architectury.platform.Platform;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.mineos.apps.BoatRacersApp;
import dev.ultreon.devices.mineos.apps.NoteStashApp;
import dev.ultreon.devices.mineos.apps.PixelPainterApp;
import dev.ultreon.devices.mineos.apps.auction.MineBayApp;
import dev.ultreon.devices.mineos.apps.email.EmailApp;
import dev.ultreon.devices.mineos.apps.gitweb.GitWebApp;
import dev.ultreon.devices.mineos.apps.snake.SnakeApp;
import dev.ultreon.devices.mineos.apps.system.*;
import dev.ultreon.devices.mineos.apps.themes.ThemesApp;
import net.minecraft.resources.ResourceLocation;

public class BuiltinApps {
    public static void registerBuiltinApps() {
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "diagnostics"), () -> DiagnosticsApp::new, true);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "settings"), () -> SettingsApp::new, true);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "file_browser"), () -> FileBrowserApp::new, true);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "gitweb"), () -> GitWebApp::new, false);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "note_stash"), () -> NoteStashApp::new, false);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "pixel_painter"), () -> PixelPainterApp::new, false);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "ender_mail"), () -> EmailApp::new, false);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "app_store"), () -> AppStore::new, true);

        if (Platform.isDevelopmentEnvironment() || UltreonDevicesMod.EARLY_CONFIG.enableBetaApps) {
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "bank"), () -> BankApp::new, false);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "boat_racers"), () -> BoatRacersApp::new, false);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "mine_bay"), () -> MineBayApp::new, false);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "snake"), () -> SnakeApp::new, false);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "themes"), () -> ThemesApp::new, false);
        }

        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "vulnerability"), () -> VulnerabilityApp::new, true);
    }
}
