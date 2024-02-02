package com.ultreon.devices.cef;

import com.jogamp.opengl.awt.GLCanvas;
import com.ultreon.devices.Devices;
import com.ultreon.devices.Reference;
import com.ultreon.mods.lib.client.gui.screen.BaseScreen;
import com.ultreon.mods.lib.client.gui.widget.BaseButton;
import com.ultreon.mods.lib.client.gui.widget.Button;
import com.ultreon.mods.lib.client.gui.widget.Progressbar;
import com.ultreon.mods.lib.event.WindowCloseEvent;
import dev.architectury.event.EventResult;
import dev.architectury.platform.Platform;
import me.friwi.jcefmaven.*;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class CEFDownloadScreen extends BaseScreen {
    private static final Component TITLE = Component.translatable("screen.devices.cef.download.title");
    private static final ThreadGroup CEF = new ThreadGroup("CEF");
    private static final String ULTREON_BROWSER_VERSION = Devices.getModVersion();
    private static final String MINECRAFT_VERSION = Platform.getMinecraftVersion();
    private static final String CHROME_VERSION = "$jcef_version";
    private BaseButton proceedBtn;
    private Progressbar progressbar;
    private int progress = 0;

    private static Thread thread;
    private String description = "Preparing...";

    public CEFDownloadScreen(Screen back) {
        super(TITLE, back);
    }

    @Override
    protected synchronized void init() {
        progressbar = this.addRenderableWidget(new Progressbar(width / 2, height / 2, 1000));
        proceedBtn = this.addRenderableWidget(new Button(width / 2 + 91 - 50, height / 2 + 3 + 5, 50, 20, CommonComponents.GUI_PROCEED, (btn) -> back()));
        proceedBtn.active = false;

        if (thread == null) {
            thread = new Thread(CEF, this::initCef, "Init-CEF");
            thread.start();
        }
    }

    private void initCef() {
        SwingUtilities.invokeLater(() -> {
            JFrame jFrame;
            try {
                jFrame = new JFrame("Web Browser Component");
                jFrame.setAutoRequestFocus(false);
                jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                jFrame.setMaximumSize(new Dimension());
                jFrame.setMinimumSize(new Dimension());
                jFrame.setMaximizedBounds(new Rectangle());
                jFrame.setUndecorated(true);
                jFrame.setVisible(true);
                jFrame.setFocusable(false);
                jFrame.setLocation(new Point());
                jFrame.toBack();
                jFrame.setFocusableWindowState(false);
                jFrame.setExtendedState(Frame.ICONIFIED);
                BrowserFramework.init(jFrame);
            } catch (RuntimeException e) {
                String property1 = System.getProperty("java.awt.headless");
                System.out.println("property = " + property1);

                throw e;
            }

            //Create a new CefAppBuilder instance
            final var builder = new CefAppBuilder();
            final boolean useOSR = true; // Flag

            builder.getCefSettings().windowless_rendering_enabled = useOSR;

            // USE builder.setAppHandler INSTEAD OF CefApp.addAppHandler!
            // Fixes compatibility issues with MacOSX
            builder.setAppHandler(new MavenCefAppHandlerAdapter() {
                public void stateHasChanged(CefApp.CefAppState state) {

                }
            });

            //Configure the builder instance
            builder.setInstallDir(Reference.CEF_INSTALL); //Default
            builder.setProgressHandler(new MinecraftProgressHandler()); //Default
            Util.make(builder.getCefSettings(), settings -> {
                settings.windowless_rendering_enabled = useOSR; //Default - select OSR mode
                settings.log_file = "logs/cef.log";
                settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_ERROR;
                settings.locale = "en-US";
                settings.background_color = settings.new ColorType(0, 0, 0, 0);
                settings.cache_path = Reference.BROWSER_DATA.getAbsolutePath();
                settings.user_agent_product = "DevicesMod/%s Minecraft/%s Chrome/%s".formatted(ULTREON_BROWSER_VERSION, MINECRAFT_VERSION, CHROME_VERSION);
            });

            builder.addJcefArgs();

            final CefApp app;
            try {
                app = BrowserFramework.setApp(builder.build());
            } catch (IOException | UnsupportedPlatformException | InterruptedException | CefInitializationException e) {
                throw new RuntimeException(e);
            }

            CefClient client = BrowserFramework.setClient(app.createClient());
            WindowCloseEvent.EVENT.register((window, source) -> {
                client.dispose();
                return EventResult.pass();
            });
        });
    }

    @Override
    public void render(@NotNull GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        renderBackground(gfx);

        super.render(gfx, mouseX, mouseY, partialTicks);

        gfx.drawString(this.font, description, (int) (width / 2f - 91), (int) (height / 2f - 15), 0xffffff);
    }

    @Override
    public void tick() {
        progressbar.setValue(progress);
    }

    private void done() {
        progress = progressbar.getMaximum();
        proceedBtn.active = true;
    }

    @Override
    public @Nullable Vec2 getCloseButtonPos() {
        if (!proceedBtn.active) return null;
        return new Vec2(width - 10, 6);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public String getDescription() {
        return description;
    }

    private class MinecraftProgressHandler implements IProgressHandler {
        @Override
        public void handleProgress(EnumProgress state, float percent) {
            switch (state) {
                case LOCATING -> description = "Locating...";
                case INSTALL -> {
                    if (percent < 0f) {
                        description = "Installing the Chrome Embedded Framework...";
                    } else {
                        description = "Installing the Chrome Embedded Framework... (" + (int)percent + "% complete)";
                    }
                }
                case DOWNLOADING -> {
                    if (percent < 0f) {
                        description = "Downloading the Chrome Embedded Framework...";
                    } else {
                        description = "Downloading the Chrome Embedded Framework... (" + (int)percent + "% complete)";
                    }
                }
                case EXTRACTING -> {
                    if (percent < 0f) {
                        description = "Extracting the Chrome Embedded Framework...";
                    } else {
                        description = "Extracting the Chrome Embedded Framework... (" + (int)percent + "% complete)";
                    }
                }
                case INITIALIZING -> description = "Initializing...";
                case INITIALIZED -> {
                    description = "Initialized!";
                    done();
                }
            }

            if (percent >= 0f) {
                progress = (int) (progressbar.getMaximum() * (percent / 100f));
            }
        }
    }
}
