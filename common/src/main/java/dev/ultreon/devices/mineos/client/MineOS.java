package dev.ultreon.devices.mineos.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import dev.ultreon.devices.UltreonDevicesMod;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.api.app.component.Image;
import dev.ultreon.devices.api.io.Drive;
import dev.ultreon.devices.api.task.Callback;
import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.api.video.VideoInfo;
import dev.ultreon.devices.block.entity.ComputerBlockEntity;
import dev.ultreon.devices.client.Display;
import dev.ultreon.devices.core.task.TaskInstallApp;
import dev.ultreon.devices.api.bios.InterruptData;
import dev.ultreon.devices.api.util.Color;
import dev.ultreon.devices.mineos.MineOSSystem;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.mineos.apps.system.DiagnosticsApp;
import dev.ultreon.devices.mineos.apps.system.DisplayResolution;
import dev.ultreon.devices.mineos.apps.system.PredefinedResolution;
import dev.ultreon.devices.mineos.apps.system.SystemApp;
import dev.ultreon.devices.mineos.apps.system.component.FileBrowser;
import dev.ultreon.devices.mineos.apps.system.task.TaskUpdateApplicationData;
import dev.ultreon.devices.mineos.apps.system.task.TaskUpdateSystemData;
import dev.ultreon.devices.util.FrameBuffer;
import dev.ultreon.devices.util.GLHelper;
import dev.architectury.injectables.annotations.PlatformOnly;
import dev.architectury.platform.Mod;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import dev.ultreon.devices.api.bios.Bios;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.api.app.Dialog;
import dev.ultreon.devices.api.app.SystemAccessor;
import dev.ultreon.devices.api.io.File;
import dev.ultreon.devices.api.utils.OnlineRequest;
import dev.ultreon.devices.api.video.CustomResolution;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.GuiGraphics;
import dev.ultreon.devices.api.os.OperatingSystem;
import dev.ultreon.devices.api.os.ShutdownSource;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//TODO Intro message (created by mrcrayfish, donate here)

/**
 * MineOS GUI class.
 *
 * @author MrCrayfish, XyperCode
 */
public class MineOS extends Screen implements OperatingSystem, MineOSSystem {
    public static final int ID = 1;
    public static final ResourceLocation ICON_TEXTURES = UltreonDevicesMod.res("textures/atlas/app_icons.png");
    public static final int ICON_SIZE = 14;
    private static final List<Application> APPLICATIONS = new ArrayList<>();
    private static final List<ResourceLocation> WALLPAPERS = new ArrayList<>();
    private static final List<Runnable> tasks = new CopyOnWriteArrayList<>();
    private static final Map<UUID, MineOS> instances = new HashMap<>();
    private static Font font;

    private final MineOSKernel kernel = new MineOSKernel();
    private final boolean worldLess;
    private final ComputerBlockEntity computer;
    private final IntArraySet pressed = new IntArraySet();
    private final Bios bios;
    private Double dragWindowFromX;
    private Double dragWindowFromY;
    private VideoInfo videoInfo;

    private MineOSSystem system;
    private BlockPos pos;
    private Drive mainDrive;
    private Settings settings;
    private TaskBar bar;
    CopyOnWriteArrayList<Window<?>> windows;
    ConcurrentHashMap<UUID, Window<?>> windowById = new ConcurrentHashMap<>();
    private CompoundTag appData;
    private CompoundTag systemData;
    protected List<AppInfo> installedApps = new ArrayList<>();
    private Layout context = null;
    private Wallpaper currentWallpaper;
    private boolean dragging = false;
    private Image wallpaper;
    private Layout wallpaperLayout;
    private BSOD bsod;
    private Display display;

    /**
     * Creates a new laptop GUI.
     */
    public MineOS(@Nullable ComputerBlockEntity computer, Bios bios) {
        this(computer, bios, computer == null);
    }

    /**
     * Creates a new laptop GUI.
     */
    public MineOS(@Nullable ComputerBlockEntity computer, Bios bios, boolean worldLess) {
        super(Component.literal("MineOS"));
        this.minecraft = Minecraft.getInstance();

        this.computer = computer;
        this.bios = bios;
        this.worldLess = worldLess;

        this.boot(bios);

        if (computer != null) {
            MineOS.instances.put(bios.getDeviceId(), this);
        }
    }

    @PlatformOnly("fabric")
    public static List<Application> getApplicationsForFabric() {
        return APPLICATIONS;
    }

    public static List<ResourceLocation> getWallpapers() {
        return ImmutableList.copyOf(WALLPAPERS);
    }

    public static Font getFont() {
        if (font == null) {
            font = Minecraft.getInstance().font;
        }
        return font;
    }

    public static MineOS get(UUID id) {
        return instances.get(id);
    }

    public static MineOS getOpened() {
        OperatingSystem os = Display.get().getOS();
        if (os instanceof MineOS mineOS) {
            return mineOS;
        }

        throw new ClassCastException("Cannot cast " + os.getClass().getName() + " to " + MineOS.class.getName());
    }

    public int getScreenWidth() {
        return videoInfo.getResolution().width();
    }

    public int getScreenHeight() {
        return videoInfo.getResolution().height();
    }

    public DisplayResolution getResolution() {
        return videoInfo.getResolution();
    }

    public CompoundTag getModSystemTag(Mod mod) {
        return getModSystemTag(mod.getModId());
    }

    public CompoundTag getModSystemTag(String modId) {
        CompoundTag mods = systemData.getCompound("Mods");
        systemData.put("Mods", mods);
        CompoundTag mod = mods.getCompound(modId);
        mods.put(modId, mod);
        return mod;
    }

    @Override
    public boolean isWorldLess() {
        return worldLess;
    }

    @Override
    public Screen getScreen() {
        return this;
    }

    /**
     * Returns the position of the laptop the player is currently using. This method can ONLY be
     * called when the laptop GUI is open, otherwise it will return a null position.
     *
     * @return the position of the laptop currently in use
     */
    @Nullable
    public BlockPos getPos() {
        return pos;
    }

    /**
     * Add a wallpaper to the list of available wallpapers.
     *
     * @param wallpaper location to the wallpaper texture, if null the wallpaper will not be added.
     */
    public static void addWallpaper(ResourceLocation wallpaper) {
        if (wallpaper != null) {
            WALLPAPERS.add(wallpaper);
        }
    }

    public MineOSSystem getSystem() {
        return system;
    }

    @Nullable
    @Deprecated
    public Drive getMainDrive() {
        return bios.getMainDrive();
    }

    @Deprecated
    public void setMainDrive(Drive mainDrive) {
        bios.setMainDrive(mainDrive);
    }

    /**
     * Run a task later in render thread.
     *
     * @param task the task to run.
     */
    public static void runLater(Runnable task) {
        tasks.add(task);
    }

    /**
     * Initialize the MineOS GUI.
     */
    @Override
    public void init(GuiGraphics graphics) {
        bar.init(0, getScreenHeight() - bar.getHeight());

        installedApps.clear();
        ListTag list = systemData.getList("InstalledApps", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            AppInfo info = ApplicationManager.getApplication(ResourceLocation.tryParse(list.getString(i)));
            if (info != null) {
                installedApps.add(info);
            }
        }
        installedApps.sort(AppInfo.SORT_NAME);
        if (UltreonDevicesMod.get().isDebug()) {
            installedApps.addAll(ApplicationManager.getAvailableApplications());
        }
    }

    @Override
    public void boot(Bios bios) {
        // MineOS data.
        this.appData = computer.getApplicationData();
        this.systemData = computer.getSystemData();

        CompoundTag videoInfoData = this.systemData.getCompound("videoInfo");
        this.videoInfo = new VideoInfo(videoInfoData);

        // Windows
        this.windows = new CopyOnWriteArrayList<>() {
            @Override
            public Window<?> get(int index) {
                try {
                    return super.get(index);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public boolean add(Window<?> window) {
                window.removed = false;
                return super.add(window);
            }
        };

        // Settings etc.
        this.settings = Settings.fromTag(systemData.getCompound("Settings"));

        // GUI Components
        CompoundTag taskBarTag = systemData.getCompound("TaskBar");
        systemData.put("TaskBar", taskBarTag);
        this.bar = new TaskBar(this, taskBarTag);

        // Wallpaper stuff
        this.currentWallpaper = systemData.contains("CurrentWallpaper", 10) ? new Wallpaper(systemData.getCompound("CurrentWallpaper")) : null;
        if (this.currentWallpaper == null) this.currentWallpaper = new Wallpaper(0);
        this.system = this;
        this.pos = computer.getBlockPos();
        this.wallpaperLayout = new Layout(getScreenWidth(), getScreenHeight());
        this.wallpaper = new Image(0, 0, getScreenWidth(), getScreenHeight());
        if (currentWallpaper.isBuiltIn()) {
            wallpaper.setImage(WALLPAPERS.get(currentWallpaper.location));
        } else {
            wallpaper.setImage(currentWallpaper.url);
        }
        this.wallpaperLayout.addComponent(this.wallpaper);
        this.wallpaperLayout.handleLoad();
    }

    @Override
    public void render(GuiGraphics display) {

    }

    @Override
    public void onShutdownRequest(ShutdownSource source) {

    }

    @Override
    public boolean onBiosInterrupt(InterruptData interrupt) {
        return false;
    }

    private int getDeviceWidth() {
        return getScreenWidth();
    }

    private int getDeviceHeight() {
        return getScreenHeight();
    }

    @Override
    public void removed() {
        /* Close all windows and sendTask application data */
        for (int i = 0; i < windows.size(); i++) {
            Window<?> window = windows.get(i);
            if (window != null) {
                window.close();
                i--;
            }
        }

        /* Send system data */
        this.updateSystemData();

        this.pos = null;
        this.system = null;
        this.mainDrive = null;
    }

    private void updateSystemData() {
        systemData.put("CurrentWallpaper", currentWallpaper.serialize());
        systemData.put("Settings", settings.toTag());
        systemData.put("TaskBar", bar.serialize());

        ListTag tagListApps = new ListTag();
        installedApps.forEach(info -> tagListApps.add(StringTag.valueOf(info.getFormattedId())));
        systemData.put("InstalledApps", tagListApps);

        TaskManager.sendTask(new TaskUpdateSystemData(pos, systemData));
    }

    /**
     * Handles Minecraft GUI resizing.
     *
     * @param width  the new width
     * @param height the new height
     */
    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);

        if (videoInfo.getResolution().width() > width || videoInfo.getResolution().height() > height) {
            videoInfo.setResolution(new CustomResolution(width, height));
        }

        revalidateDisplay();
    }

    public void revalidateDisplay() {
        wallpaper.componentWidth = videoInfo.getResolution().width();
        wallpaper.componentHeight = videoInfo.getResolution().height();
        wallpaperLayout.width = videoInfo.getResolution().width();
        wallpaperLayout.height = videoInfo.getResolution().height();
        wallpaperLayout.updateComponents(0, 0);

        for (var window : windows) {
            if (window != null) {
                window.content.markForLayoutUpdate();
            }
        }
    }

    /**
     * Ticking the laptop.
     */
    @Override
    public void tick() {
        try {
            bar.onTick();

            for (Window<?> window : windows) {
                if (window != null) {
                    window.onTick();
//                    if (window.removed) {
//                        java.lang.DebugLog.log("REMOVED " + window);
//                        windows.remove(window);
//                        i--;
//                    }
                }
            }

            FileBrowser.refreshList = false;
        } catch (Exception e) {
            bsod(e);
        }
    }

    @Override
    public void render(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        if (bsod != null) {
            renderBsod(graphics, mouseX, mouseY, partialTicks);
            return;
        }

        PoseStack.Pose last = graphics.pose().last();

        this.renderBackground(graphics, mouseX, mouseY, partialTicks);

        try {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 1000);

            renderMineOS(graphics, mouseX, mouseY, partialTicks);
            graphics.pose().popPose();
        } catch (NullPointerException e) {
            while (graphics.pose().last() != last) {
                graphics.pose().popPose();
            }
            RenderSystem.disableScissor();
            bsod(e);// null
        } catch (Exception e) {
            while (graphics.pose().last() != last) {
                graphics.pose().popPose();
            }
            RenderSystem.disableScissor();
            bsod(e);
        }

        while (graphics.pose().last() != last) {
            UltreonDevicesMod.LOGGER.warn("Pose stack leakage: {}", graphics.pose().last());
            graphics.pose().popPose();
        }

        if (GLHelper.clearScissorStack()) {
            UltreonDevicesMod.LOGGER.debug("Scissor stack leakage!");
        }
    }

    public void renderBsod(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, float partialTicks) {
        graphics.fill(0, 0, getDeviceWidth(), getDeviceHeight(), new Color(0, 0, 255).getRGB());
        var bo = new ByteArrayOutputStream();

        double scale = Minecraft.getInstance().getWindow().getGuiScale();

        var b = new PrintStream(bo);
        bsod.throwable.printStackTrace(b);
        var str = bo.toString();
        drawLines(graphics, MineOS.getFont(), str, 0, getFont().lineHeight * 2, (int) ((getDeviceWidth()) * scale), new Color(255, 255, 255).getRGB());
        graphics.pose().pushPose();
        graphics.pose().scale(2, 2, 0);
        graphics.pose().translate((0) / 2f, (0) / 2f, 0);
        graphics.drawString(getFont(), "System has crashed!", 0, 0, new Color(255, 255, 255).getRGB());
        graphics.pose().popPose();
    }

    public void drawLines(GuiGraphics graphics, Font font, String text, int x, int y, int width, int color) {
        var lines = new ArrayList<String>();
        font.getSplitter().splitLines(FormattedText.of(text.replaceAll("\r\n", "\n").replaceAll("\r", "\n")), width, Style.EMPTY).forEach(b -> lines.add(b.getString()));
        var totalTextHeight = font.lineHeight * lines.size();
        var textScale = (videoInfo.getResolution().height() - 10 - (getFont().lineHeight * 2)) / (float) totalTextHeight;
        textScale = (float) (1f / Minecraft.getInstance().getWindow().getGuiScale());
        textScale = Math.max(0.5f, textScale);
        graphics.pose().pushPose();
        graphics.pose().scale(textScale, textScale, 1);
        graphics.pose().translate(x / textScale, (y + 3) / textScale, 0);
        //poseStack.translate();
        var lineNr = 0;
        for (String s : lines) {
            graphics.drawString(font, s.replaceAll("\t", "    "), 0, lineNr * font.lineHeight, color);
            lineNr++;
        }
        graphics.pose().popPose();
    }

    /**
     * Render the laptop screen.
     *
     * @param graphics     gui graphics helper
     * @param mouseX       the current mouse X position.
     * @param mouseY       the current mouse Y position.
     * @param partialTicks the rendering partial ticks that forge give use (which is useless here).
     */
    public void renderMineOS(final @NotNull GuiGraphics graphics, final int mouseX, final int mouseY, float partialTicks) {
        // Fixes the strange partialTicks that Forge decided to give us
        for (Runnable task : tasks) {
            task.run();
        }

        GLHelper.pushScissor(graphics, 0, 0, videoInfo.getResolution().width(), videoInfo.getResolution().height());
        //*******************//
        //     Wallpaper     //
        //*******************//
        //RenderSystem.setShaderTexture(0, WALLPAPERS.get(currentWallpaper));
        //RenderUtil.drawRectWithTexture(pose, 0 + 0, 0 + 0, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 512, 288);
        Image.CACHE.forEach((s, cachedImage) -> cachedImage.delete());
        this.wallpaperLayout.render(graphics, this, this.minecraft, 0, 0, mouseX, mouseY, true, partialTicks);
        boolean insideContext = false;
        if (context != null) {
            insideContext = isMouseInside(mouseX, mouseY, context.xPosition, context.yPosition, context.xPosition + context.width, context.yPosition + context.height);
        }

        //****************//
        //     Window     //
        //****************//
        graphics.pose().pushPose();
        {
            //   Window<?>[] windows1 = Arrays.stream(windows.toArray()).filter(Objects::nonNull).toArray(Window<?>[]::new);
            for (int i = windows.size() - 1; i >= 0; i--) {
                var window = windows.get(i);
                if (window != null) {
                    PoseStack.Pose last = graphics.pose().last();
                    try {
                        if (i == 0)
                            renderWin(graphics, mouseX, mouseY, partialTicks, window, this, minecraft, 0, 0, insideContext);
                        else
                            renderWin(graphics, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTicks, window, this, minecraft, 0, 0, insideContext);
                    } catch (Exception e) {
                        while (graphics.pose().last() != last) graphics.pose().popPose();

                        RenderSystem.disableScissor();
                        UltreonDevicesMod.LOGGER.error("Error rendering window", e);
                        Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
                        message.setTitle("Error");
                        CompoundTag intent = new CompoundTag();
                        if (window.content instanceof Application app) {
                            AppInfo info = app.getInfo();
                            if (info != null) {
                                intent.putString("name", info.getName());
                            }
                            openApplication(ApplicationManager.getApplication(UltreonDevicesMod.res("diagnostics")), intent);
                            closeApplication(app);
                        }
                    }
                    graphics.pose().translate(0, 0, 400);
                }
            }
        }
        bar.render(graphics, this, minecraft, 0, getDeviceHeight() - TaskBar.BAR_HEIGHT, mouseX, mouseY, partialTicks);

        graphics.pose().translate(0, 0, 200);
        if (context != null) {
            context.render(graphics, this, minecraft, context.xPosition, context.yPosition, mouseX, mouseY, true, partialTicks);
        }

        graphics.pose().popPose();

        //****************************//
        // Render the Application Bar //
        //****************************//
        Image.CACHE.entrySet().removeIf(entry -> {
            Image.CachedImage cachedImage = entry.getValue();
            if (cachedImage.isDynamic() && cachedImage.isPendingDeletion()) {
                int texture = cachedImage.getTextureId();
                if (texture != -1) {
                    RenderSystem.deleteTexture(texture);
                }
                return true;
            }
            return false;
        });

        GLHelper.popScissor();

        GLHelper.clearScissorStack();
    }

    private static void renderWin(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Window<?> window, MineOS mineOS, Minecraft minecraft1, int x, int y, boolean insideContext) {
//        FrameBuffer frameBuffer = window.getFrameBuffer();
//        frameBuffer.begin();
        window.render(graphics, mineOS, minecraft1, window.offsetX, window.offsetY, mouseX, mouseY, partialTicks, !insideContext);
//        frameBuffer.end();
//
//        frameBuffer.blit(graphics, 0, 0, window.width, window.height);
    }

    private boolean isMouseInside(int mouseX, int mouseY, int startX, int startY, int endX, int endY) {
        return mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY;
    }

    protected void bsod(Throwable e) {
        this.bsod = new BSOD(e);
        UltreonDevicesMod.LOGGER.error("BSOD", e);
    }

    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setDisplayResolution(PredefinedResolution newValue) {
        if (this.videoInfo != null) {
            this.videoInfo.setResolution(newValue);
            this.display.setResolution(newValue);
        }
    }

    public void revalidate() {

    }

    public Bios getBios() {
        return bios;
    }

    @Override
    public void connectDisplay(Display display) {
        this.display = display;
    }

    @Override
    public void disconnectDisplay() {
        this.display = null;
    }

    public UUID generateWindowId() {
        UUID id;
        int attempts = 0;
        do {
            id = UUID.randomUUID();
            attempts++;

            if (attempts > 1000) {
                throw new IllegalStateException("Failed to generate window id after 500 attempts");
            }
        } while (windowById.containsKey(id));

        if (attempts > 50) {
            UltreonDevicesMod.LOGGER.warn("Slowly generated window ID after {} attempts. Is the window manager overloaded?", attempts);
        }

        return id;
    }

    private static final class BSOD {
        private final Throwable throwable;

        public BSOD(Throwable e) {
            this.throwable = e;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.context != null) {
            int dropdownX = context.xPosition;
            int dropdownY = context.yPosition;
            if (isMouseInside((int) mouseX, (int) mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
                this.context.handleMouseClick((int) mouseX, (int) mouseY, mouseButton);
                return false;
            } else {
                this.context = null;
            }
        }

        this.bar.handleClick(this, 0, getScreenHeight() - TaskBar.BAR_HEIGHT, (int) mouseX, (int) mouseY, mouseButton);

        for (int i = 0; i < windows.size(); i++) {
            Window<Application> window = (Window<Application>) windows.get(i);
            if (window != null) {
                try {
                    Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
                    if (isMouseWithinWindow((int) mouseX, (int) mouseY, window) || isMouseWithinWindow((int) mouseX, (int) mouseY, dialogWindow)) {
                        windows.remove(i);
                        i--;
                        updateWindowStack();
                        windows.add(0, window);

                        window.handleMouseClick(this, 0, 0, (int) mouseX, (int) mouseY, mouseButton);

                        if (isMouseWithinWindowBar((int) mouseX, (int) mouseY, dialogWindow)) {
                            dragWindowFromX = mouseX - dialogWindow.offsetX;
                            dragWindowFromY = mouseY - dialogWindow.offsetY;
                            this.dragging = true;
                            return false;
                        }

                        if (isMouseWithinWindowBar((int) mouseX, (int) mouseY, window) && dialogWindow == null) {
                            dragWindowFromX = mouseX - window.offsetX;
                            dragWindowFromY = mouseY - window.offsetY;
                            this.dragging = true;
                            return false;
                        }
                        break;
                    }
                } catch (Exception e) {
                    UltreonDevicesMod.LOGGER.error("An error has occurred.", e);
                    Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
                    message.setTitle("Error");
                    if (windows.isEmpty()) {
                        CompoundTag intent = new CompoundTag();
                        AppInfo info = window.content.getInfo();
                        if (info != null) {
                            intent.putString("name", info.getName());
                        }
                        openApplication(ApplicationManager.getApplication(UltreonDevicesMod.id("diagnostics")), intent);
                    } else {
                        window.openDialog(message);
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.dragging = false;
        dragWindowFromX = null;
        dragWindowFromY = null;
        try {
            if (this.context != null) {
                int dropdownX = context.xPosition;
                int dropdownY = context.yPosition;
                if (isMouseInside((int) mouseX, (int) mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
                    this.context.handleMouseRelease((int) mouseX, (int) mouseY, state);
                }
            } else if (windows.get(0) != null) {
                windows.get(0).handleMouseRelease((int) mouseX, (int) mouseY, state);
            }
        } catch (Exception e) {
            UltreonDevicesMod.LOGGER.error("An error has occurred.", e);
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.get(0).openDialog(message);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int state) {
        return false;
    }

    @Override
    public void afterKeyboardAction() {
//        if (Keyboard.getEventKeyState()) {
//            char pressed = Keyboard.getEventCharacter();
//            int code = Keyboard.getEventKey();
//
//            if (windows[0] != null) {
//                windows[0].handleKeyTyped(pressed, code);
//            }
//
////            super.charTyped(pressed, code);
//        } else {
//        }

        // Todo - handle key presses
//        this.minecraft.dispatchKeypresses();
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean override = false;
        try {
            if (!override && windows.get(0) != null)
                windows.get(0).handleCharTyped(codePoint, modifiers);
        } catch (Exception e) {
            UltreonDevicesMod.LOGGER.error("An error has occurred.", e);
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.get(0).openDialog(message);
        }
        return override;
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        final boolean override = false;

        try {
            if (!pressed.contains(keyCode) && !override && windows.get(0) != null) {
                windows.get(0).handleKeyPressed(keyCode, scanCode, modifiers);
            }
        } catch (Exception e) {
            UltreonDevicesMod.LOGGER.error("An error has occurred.", e);
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.get(0).openDialog(message);
        }
        pressed.add(keyCode);
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        pressed.remove(keyCode);

        try {
            if (keyCode >= 32 && keyCode < 256 && windows.get(0) != null) {
                windows.get(0).handleKeyReleased(keyCode, scanCode, modifiers);
                return true;
            }
        } catch (Exception e) {
            UltreonDevicesMod.LOGGER.error("An error has occurred.", e);
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.get(0).openDialog(message);
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        try {
            if (this.context != null) {
                int dropdownX = context.xPosition;
                int dropdownY = context.yPosition;
                if (isMouseInside((int) mouseX, (int) mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
                    this.context.handleMouseDrag((int) mouseX, (int) mouseY, button);
                }
                return true;
            }

            if (windows.get(0) != null) {
                Window<Application> window = (Window<Application>) windows.get(0);
                Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
                if (dragging) {
                    if (isMouseOnScreen((int) mouseX, (int) mouseY) && dragWindowFromX != null && dragWindowFromY != null) {
                        Objects.requireNonNullElse(dialogWindow, window).handleWindowMove(0, 0, (int) ((dragX + mouseX) - dragWindowFromX), (int) ((dragY + mouseY) - dragWindowFromY));
                    } else {
                        dragging = false;
                    }
                } else {
                    if (isMouseWithinWindow((int) mouseX, (int) mouseY, window) || isMouseWithinWindow((int) mouseX, (int) mouseY, dialogWindow)) {
                        window.handleMouseDrag((int) mouseX, (int) mouseY, button);
                    }
                }
            }
        } catch (Exception e) {
            UltreonDevicesMod.LOGGER.error("An error has occurred.", e);
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.get(0).openDialog(message);
        }
        return true;
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (deltaY != 0) {
            try {
                if (windows.get(0) != null) {
                    windows.get(0).handleMouseScroll((int) mouseX, (int) mouseY, deltaY, deltaY >= 0);
                }
            } catch (Exception e) {
                UltreonDevicesMod.LOGGER.error("An error has occurred.", e);
                Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
                message.setTitle("Error");
                windows.get(0).openDialog(message);
            }
        }
        return true;
    }

    public void renderComponentTooltip(@NotNull GuiGraphics graphics, @NotNull List<Component> tooltips, int x, int y) {
        graphics.renderComponentTooltip(font, tooltips, x, y);
    }

    @SuppressWarnings("ReassignedVariable")
    public Pair<Application, Boolean> sendApplicationToFront(AppInfo info) {
        int i = 0;
        for (; i < windows.size(); i++) {
            Window<?> window = windows.get(i);
            if (window != null && window.content instanceof Application application && application.getInfo() == info) {
                windows.remove(i);
                updateWindowStack();
                windows.add(0, window);
                return new Pair<>(application, true);
            }
        }
        return new Pair<>(null, false);
    }

    @Override
    public Application openApplication(AppInfo info) {
        return openApplication(info, (CompoundTag) null);
    }

    @Override
    public Application openApplication(AppInfo info, CompoundTag intentTag) {
        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        Application[] app = new Application[]{null};
        optional.ifPresent(application -> app[0] = openApplication(application, intentTag));
        return app[0];
    }

    private Application openApplication(Application app, CompoundTag intent) {
        if (!(app instanceof DiagnosticsApp)) {
            if (isApplicationNotInstalled(app.getInfo()))
                return null;

            if (isInvalidApplication(app.getInfo()))
                return null;
        }

        try {
            var q = sendApplicationToFront(app.getInfo());
            if (q.getSecond())
                return q.getFirst();

            if (app instanceof SystemApp) {
                ((SystemApp) app).setOS(this);
            }

            if (app instanceof SystemAccessor) {
                ((SystemAccessor) app).sendSystem(this);
            }

            Window<Application> window = new Window<>(app, this);
            window.init((window.width - getScreenWidth()) / 2, (window.height - getScreenHeight()) / 2, intent);

            if (appData.contains(app.getInfo().getFormattedId())) {
                app.load(appData.getCompound(app.getInfo().getFormattedId()));
            }

            if (app.getCurrentLayout() == null) {
                app.restoreDefaultLayout();
            }

            addWindow(window);

            this.kernel.playSound(SoundEvents.UI_BUTTON_CLICK.value());
        } catch (Exception e) {
            UltreonDevicesMod.LOGGER.error("Failed to open application", e);
            AppInfo info = ApplicationManager.getApplication(UltreonDevicesMod.res("diagnostics"));
            openApplication(info);
        }
        return app;
    }

    public Pair<Application, Boolean> openApplication(AppInfo info, File file) {
        if (isApplicationNotInstalled(info))
            return new Pair<>(null, false);

        if (isInvalidApplication(info))
            return new Pair<>(null, false);

        try {
            Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
            if (optional.isPresent()) {
                Application application = optional.get();
                boolean alreadyRunning = isApplicationRunning(info);
                openApplication(application, null);
                if (isApplicationRunning(info)) {
                    if (!application.handleFile(file)) {
                        if (!alreadyRunning) {
                            closeApplication(application);
                        }
                        return new Pair<>(application, false);
                    }
                    return new Pair<>(application, true);
                }
            }
        } catch (Exception e) {
            UltreonDevicesMod.LOGGER.error("Failed to open application", e);
            AppInfo info1 = ApplicationManager.getApplication(UltreonDevicesMod.res("diagnostics"));
            openApplication(info1);
        }
        return new Pair<>(null, true);
    }

    public void closeApplication(AppInfo info) {
        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        optional.ifPresent(this::closeApplication);
    }

    @SuppressWarnings("unchecked")
    private void closeApplication(Application app) {
        for (int i = 0; i < windows.size(); i++) {
            Window<Application> window = (Window<Application>) windows.get(i);
            if (window != null) {
                if (window.content.getInfo().equals(app.getInfo())) {
                    if (app.isDirty()) {
                        CompoundTag container = new CompoundTag();
                        app.save(container);
                        app.clean();
                        appData.put(app.getInfo().getFormattedId(), container);
                        TaskManager.sendTask(new TaskUpdateApplicationData(pos.getX(), pos.getY(), pos.getZ(), app.getInfo().getFormattedId(), container));
                    }

                    if (app instanceof SystemApp) {
                        ((SystemApp) app).setOS(null);
                    }

                    window.handleClose();
                    windows.remove(i);
                    return;
                }
            }
        }
    }

    private void addWindow(Window<Application> window) {
        if (hasReachedWindowLimit())
            return;

        updateWindowStack();
        windows.add(0, window);
        windowById.put(window.windowId, window);
    }

    private void updateWindowStack() {
        for (int i = windows.size() - 1; i >= 0; i--) {
            if (windows.get(i) != null) {
                if (i + 1 < windows.size()) {
                    if (i == 0 || windows.get(i - 1) != null) {
                        if (windows.get(i + 1) == null) {
                            windows.add(i + 1, windows.get(i));
                            windows.remove(i);
                        }
                    }
                }
            }
        }
    }

    private boolean hasReachedWindowLimit() {
//        for (Window<?> window : windows) {
//           // if (window == null) return false;
//        }
        return false;
    }

    private boolean isMouseOnScreen(int mouseX, int mouseY) {
        return isMouseInside(mouseX, mouseY, 0, 0, getScreenWidth(), getScreenHeight());
    }

    private boolean isMouseWithinWindowBar(int mouseX, int mouseY, Window<?> window) {
        if (window == null) return false;
        return isMouseInside(mouseX, mouseY, window.offsetX + 1, window.offsetY + 1, window.offsetX + window.width - 1, window.offsetY + 11);
    }

    private boolean isMouseWithinWindow(int mouseX, int mouseY, Window<?> window) {
        if (window == null) return false;
        return isMouseInside(mouseX, mouseY, window.offsetX, window.offsetY, window.offsetX + window.width, window.offsetY + window.height);
    }

    public boolean isMouseWithinApp(int mouseX, int mouseY, Window<?> window) {
        return isMouseInside(mouseX, mouseY, window.offsetX + 1, window.offsetY + 3, window.offsetX + window.width - 1, window.offsetY + window.height - 1);
    }

    public boolean isApplicationRunning(AppInfo info) {
        for (Window<?> window : windows) {
            if (window != null && ((Application) window.content).getInfo() == info) {
                return true;
            }
        }
        return false;
    }

    public void nextWallpaper() {
        if (!currentWallpaper.isBuiltIn()) return;
        if (currentWallpaper.location + 1 < WALLPAPERS.size()) {
            this.currentWallpaper = new Wallpaper(currentWallpaper.location + 1);
        }
        wallpaperUpdated();
    }

    public void prevWallpaper() {
        if (currentWallpaper.location - 1 >= 0) {
            this.currentWallpaper = new Wallpaper(currentWallpaper.location - 1);
        }
        wallpaperUpdated();
    }

    private void wallpaperUpdated() {
        if (currentWallpaper.isBuiltIn()) {
            wallpaper.setImage(WALLPAPERS.get(currentWallpaper.location));
        } else {
            wallpaper.setImage(currentWallpaper.url);
        }
    }

    public void setWallpaper(String url) {
        currentWallpaper = new Wallpaper(url);
        wallpaperUpdated();
    }

    public void setWallpaper(int wall) {
        currentWallpaper = new Wallpaper(wall);
        wallpaperUpdated();
    }

    public Wallpaper getCurrentWallpaper() {
        return currentWallpaper;
    }

    public List<ResourceLocation> getWallapapers() {
        return List.copyOf(WALLPAPERS);
    }

    @Nullable
    public Application getApplication(String appId) {
        return APPLICATIONS.stream().filter(app -> app.getInfo().getFormattedId().equals(appId)).findFirst().orElse(null);
    }

    @Override
    public List<AppInfo> getInstalledApplications() {
        return List.copyOf(installedApps);
    }

    public boolean isApplicationInstalled(AppInfo info) {
        return info.isSystemApp() || installedApps.contains(info);
    }

    public boolean isApplicationNotInstalled(AppInfo info) {
        return !isApplicationInstalled(info);
    }

    private boolean isValidApplication(AppInfo info) {
        if (UltreonDevicesMod.hasAllowedApplications()) {
            return UltreonDevicesMod.getAllowedApplications().contains(info);
        }
        return true;
    }

    private boolean isInvalidApplication(AppInfo info) {
        return !isValidApplication(info);
    }

    public void installApplication(AppInfo info, @Nullable Callback<Object> callback) {
        if (isValidApplication(info)) {
            Task task = new TaskInstallApp(info, pos, true);
            task.setCallback((tag, success) ->
            {
                if (success) {
                    installedApps.add(info);
                    installedApps.sort(AppInfo.SORT_NAME);
                }
                if (callback != null) {
                    callback.execute(null, success);
                }
            });
            TaskManager.sendTask(task);
        }
    }

    public void removeApplication(AppInfo info, @Nullable Callback<Object> callback) {
        if (!isValidApplication(info))
            return;

        Task task = new TaskInstallApp(info, pos, false);
        task.setCallback((tag, success) ->
        {
            if (success) {
                installedApps.remove(info);
            }
            if (callback != null) {
                callback.execute(null, success);
            }
        });
        TaskManager.sendTask(task);
    }

    public List<Application> getApplications() {
        return APPLICATIONS;
    }

    public TaskBar getTaskBar() {
        return bar;
    }

    @Override
    public Settings getSettings() {
        return settings;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void openContext(Layout layout, int x, int y) {
        layout.updateComponents(x, y);
        context = layout;
        layout.init();
    }

    public boolean hasContext() {
        return context != null;
    }

    public void closeContext() {
        context = null;
        dragging = false;
    }

    public static final class Wallpaper {
        private final String url;
        private final int location;

        public String getUrl() {
            return url;
        }

        public int getLocation() {
            return location;
        }

        private Wallpaper(CompoundTag tag) {
            var url = tag.getString("url");
            var location = tag.getInt("location");
            if (tag.contains("url", 8)) {
                if (OnlineRequest.isUnsafeAddress(url)) {
                    // Reset to default wallpaper.
                    this.url = null;
                    this.location = 0;
                } else {
                    this.url = url;
                    this.location = -87;
                }
            } else {
                this.url = null;
                this.location = location;
            }
        }

        private Wallpaper(String url) {
            this.url = url;
            this.location = -87;
        }

        private Wallpaper(int location) {
            this.location = location;
            this.url = null;
        }

        public boolean isBuiltIn() {
            return this.location != -87;
        }

        public Tag serialize() {
            var a = new CompoundTag();
            if (isBuiltIn()) {
                a.putInt("location", location);
            } else {
                a.putString("url", this.url);
            }
            return a;
        }
    }
}
