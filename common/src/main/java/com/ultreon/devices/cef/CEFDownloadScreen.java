package com.ultreon.devices.cef;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.GLBuffers;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefMessageRouter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11C.*;

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
            builder.getCefSettings().windowless_rendering_enabled = useOSR; //Default - select OSR mode
            builder.getCefSettings().cache_path = Reference.BROWSER_DATA.getAbsolutePath();
            builder.getCefSettings().user_agent_product = "DevicesMod/%s Minecraft/%s Chrome/%s".formatted(ULTREON_BROWSER_VERSION, MINECRAFT_VERSION, CHROME_VERSION);

            builder.addJcefArgs();

            final CefApp app;
            try {
                app = BrowserFramework.setApp(builder.build());
            } catch (IOException | UnsupportedPlatformException | InterruptedException | CefInitializationException e) {
                throw new RuntimeException(e);
            }

            CefClient client = BrowserFramework.setClient(app.createClient());

            CefBrowser browser = client.createBrowser("www.google.com", useOSR, false);

            java.awt.Component uiComponent = browser.getUIComponent();
            GLCanvas canvas = (GLCanvas) uiComponent;
            canvas.addGLEventListener(new GLEventListener() {
                private double lastImage;

                @Override
                public void init(GLAutoDrawable drawable) {

                }

                @Override
                public void dispose(GLAutoDrawable drawable) {

                }

                @Override
                public void display(GLAutoDrawable drawable) {
                    double now = System.currentTimeMillis() / 1000.0;
                    if ((now - lastImage) > 0.02) {
                        lastImage = now;
                        BrowserFramework.bufImg = saveImage((GL3) drawable.getGL(), uiComponent.getWidth(), uiComponent.getHeight());
                    }
                }

                @Override
                public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

                }
            });

            jFrame.add(BrowserFramework.setUi(uiComponent));
            WindowCloseEvent.EVENT.register((window, source) -> {
                client.dispose();
                return EventResult.pass();
            });

            var msgRouter = CefMessageRouter.create();
            client.addMessageRouter(msgRouter);
        });
    }
    protected BufferedImage saveImage(GL3 gl3, int width, int height) {
        BufferedImage screenshot = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = screenshot.getGraphics();

        ByteBuffer buffer = GLBuffers.newDirectByteBuffer(width * height * 4);
        // be sure you are reading from the right fbo (here is supposed to be the default one)
        // bind the right buffer to read from
        gl3.glReadBuffer(GL_BACK);
        // if the width is not multiple of 4, set unpackPixel = 1
        gl3.glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                // The color are the three consecutive bytes, it's like referencing
                // to the next consecutive array elements, so we got red, green, blue.
                // red, green, blue, and so on..+ ", "
                graphics.setColor(new Color((buffer.get() & 0xff), (buffer.get() & 0xff),
                        (buffer.get() & 0xff)));
                buffer.get();   // consume alpha
                graphics.drawRect(w, height - h, 1, 1); // height - h is for flipping the image
            }
        }
        // This is one util of mine, it makes sure you clean the direct buffer
        buffer.clear();

        return screenshot;
    }
    @Override
    public void render(@NotNull GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
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
