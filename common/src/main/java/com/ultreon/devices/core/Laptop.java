package com.ultreon.devices.core;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.Devices;
import com.ultreon.devices.Reference;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.System;
import com.ultreon.devices.api.app.*;
import com.ultreon.devices.api.app.component.Image;
import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.api.io.File;
import com.ultreon.devices.api.task.Callback;
import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.core.task.TaskInstallApp;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.system.DiagnosticsApp;
import com.ultreon.devices.programs.system.SystemApp;
import com.ultreon.devices.programs.system.component.FileBrowser;
import com.ultreon.devices.programs.system.task.TaskUpdateApplicationData;
import com.ultreon.devices.programs.system.task.TaskUpdateSystemData;
import com.ultreon.devices.util.GLHelper;
import dev.architectury.injectables.annotations.PlatformOnly;
import dev.architectury.platform.Mod;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

//TODO Intro message (created by mrcrayfish, donate here)

/**
 * Laptop GUI class.
 *
 * @author MrCrayfish, Qboi123
 */
public class Laptop extends Screen implements System {
    public static final int ID = 1;
    public static final ResourceLocation ICON_TEXTURES = new ResourceLocation(Reference.MOD_ID, "textures/atlas/app_icons.png");
    public static final int ICON_SIZE = 14;
    private static final ResourceLocation LAPTOP_FONT = Devices.res("laptop");
    private static Font font;
    private static final ResourceLocation LAPTOP_GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/laptop.png");
    private static final List<Application> APPLICATIONS = new ArrayList<>();
    private static boolean worldLess;
    private static Laptop instance;

    @PlatformOnly("fabric")
    public static List<Application> getApplicationsForFabric() {
        return APPLICATIONS;
    }

    public static List<ResourceLocation> getWallpapers() {
        return ImmutableList.copyOf(WALLPAPERS);
    }

    private static final List<ResourceLocation> WALLPAPERS = new ArrayList<>();

    private static final int BORDER = 10;
    private static final int DEVICE_WIDTH = 384;
    static final int SCREEN_WIDTH = DEVICE_WIDTH - BORDER * 2;
    private static final int DEVICE_HEIGHT = 216;
    static final int SCREEN_HEIGHT = DEVICE_HEIGHT - BORDER * 2;
    private static final List<Runnable> tasks = new CopyOnWriteArrayList<>();
    private static System system;
    private static BlockPos pos;
    private static Drive mainDrive;
    private final Settings settings;
    private final TaskBar bar;
    final ArrayList<Window<?>> windows;
    private final CompoundTag appData;
    private final CompoundTag systemData;
    protected List<AppInfo> installedApps = new ArrayList<>();
    private Layout context = null;
    private Wallpaper currentWallpaper;
    private int lastMouseX, lastMouseY;
    private boolean dragging = false;
    private final IntArraySet pressed = new IntArraySet();
    private final com.ultreon.devices.api.app.component.Image wallpaper;
    private final Layout wallpaperLayout;
    private BSOD bsod;

    public static Font getFont() {
        if (font == null) {
            font = Minecraft.getInstance().font;
        }
        return font;
    }

    /**
     * Creates a new laptop GUI.
     *
     * @param laptop the block entity of the laptop in-game, if the laptop is not in-game, the level passed to it should be null.
     */
    public Laptop(LaptopBlockEntity laptop) {
        this(laptop, false);
    }

    /**
     * Creates a new laptop GUI.
     *
     * @param laptop the block entity of the laptop in-game, if the laptop is not in-game, the level passed to it should be null.
     */
    public Laptop(LaptopBlockEntity laptop, boolean worldLess) {
        super(Component.literal("Laptop"));

        instance = this;

        // Laptop data.
        this.appData = laptop.getApplicationData();
        this.systemData = laptop.getSystemData();

        // Windows
        this.windows = new ArrayList<>() {
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
        Laptop.system = this;
        Laptop.pos = laptop.getBlockPos();
        this.wallpaperLayout = new Layout(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.wallpaper = new com.ultreon.devices.api.app.component.Image(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (currentWallpaper.isBuiltIn()) {
            wallpaper.setImage(WALLPAPERS.get(currentWallpaper.location));
        } else {
            wallpaper.setImage(currentWallpaper.url);
        }
        this.wallpaperLayout.addComponent(this.wallpaper);
        this.wallpaperLayout.handleLoad();

        // World-less flag.
        Laptop.worldLess = worldLess;
    }

    public static Laptop getInstance() {
        return instance;
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

    public static boolean isWorldLess() {
        return worldLess;
    }

    /**
     * Returns the position of the laptop the player is currently using. This method can ONLY be
     * called when the laptop GUI is open, otherwise it will return a null position.
     *
     * @return the position of the laptop currently in use
     */
    @Nullable
    public static BlockPos getPos() {
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

    public static System getSystem() {
        return system;
    }

    @Nullable
    public static Drive getMainDrive() {
        return mainDrive;
    }

    public static void setMainDrive(Drive mainDrive) {
        if (Laptop.mainDrive == null) {
            Laptop.mainDrive = mainDrive;
        }
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
     * Initialize the Laptop GUI.
     */
    @Override
    public void init() {
        int posX = (width - DEVICE_WIDTH) / 2;
        int posY = (height - DEVICE_HEIGHT) / 2;
        bar.init(posX + BORDER, posY + DEVICE_HEIGHT - 28);

        installedApps.clear();
        ListTag list = systemData.getList("InstalledApps", Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            AppInfo info = ApplicationManager.getApplication(ResourceLocation.tryParse(list.getString(i)));
            if (info != null) {
                installedApps.add(info);
            }
        }
        installedApps.sort(AppInfo.SORT_NAME);
        if (Minecraft.getInstance().getConnection() == null) {
            installedApps.addAll(ApplicationManager.getAvailableApplications());
        }
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

        Laptop.pos = null;
        Laptop.system = null;
        Laptop.mainDrive = null;
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
     * @param minecraft the Minecraft instance
     * @param width     the new width
     * @param height    the new height
     */
    @Override
    public void resize(@NotNull Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
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
                    if (window.removed) {
                        //        java.lang.System.out.println("REMOVED " + window);
                        //     windows.remove(window);
                        //    i--;
                    }
                }
            }

            FileBrowser.refreshList = false;
        } catch (Exception e) {
            bsod(e);
        }
    }

    @Override
    public void render(final @NotNull PoseStack pose, final int mouseX, final int mouseY, float partialTicks) {
        if (bsod != null) {
            renderBsod(pose, mouseX, mouseY, partialTicks);
            return;
        }

        PoseStack.Pose last = pose.last();

        try {
            renderLaptop(pose, mouseX, mouseY, partialTicks);
        } catch (NullPointerException e) {
            while (pose.last() != last) {
                pose.popPose();
            }
            RenderSystem.disableScissor();
            bsod(e);// null
        } catch (Exception e) {
            while (pose.last() != last) {
                pose.popPose();
            }
            RenderSystem.disableScissor();
            bsod(e);
        }
    }

    public void renderBsod(final @NotNull PoseStack pose, final int mouseX, final int mouseY, float partialTicks) {
        renderBezels(pose, mouseX, mouseY, partialTicks);
        int posX = (width - DEVICE_WIDTH) / 2;
        int posY = (height - DEVICE_HEIGHT) / 2;
        Gui.fill(pose, posX+10, posY+10, posX + DEVICE_WIDTH-10, posY + DEVICE_HEIGHT-10, new Color(0, 0, 255).getRGB());
        var bo = new ByteArrayOutputStream();

        double scale = Minecraft.getInstance().getWindow().getGuiScale();

        var b = new PrintStream(bo);
        bsod.throwable.printStackTrace(b);
        var str = bo.toString();
        drawLines(pose, Laptop.getFont(), str, posX+10, posY+10+getFont().lineHeight*2, (int) ((DEVICE_WIDTH - 10) * scale), new Color(255, 255, 255).getRGB());
        pose.pushPose();
        pose.scale(2, 2, 0);
        pose.translate((posX+10)/2f,(posY+10)/2f,0);
        drawString(pose, getFont(), "System has crashed!", 0, 0, new Color(255, 255, 255).getRGB());
        pose.popPose();
    }

    public static void drawLines(PoseStack poseStack, Font font, String text, int x, int y, int width, int color) {
        var lines = new ArrayList<String>();
        font.getSplitter().splitLines(FormattedText.of(text.replaceAll("\r\n", "\n").replaceAll("\r", "\n")), width, Style.EMPTY).forEach(b -> lines.add(b.getString()));
        var totalTextHeight = font.lineHeight*lines.size();
        var textScale = (DEVICE_HEIGHT-20-(getFont().lineHeight*2))/(float)totalTextHeight;
        textScale = (float) (1f / Minecraft.getInstance().getWindow().getGuiScale());
        textScale = Math.max(0.5f, textScale);
        poseStack.pushPose();
        poseStack.scale(textScale, textScale, 1);
        poseStack.translate(x / textScale, (y+3)/textScale, 0);
        //poseStack.translate();
        var lineNr = 0;
        for (String s : lines) {
            font.draw(poseStack, s.replaceAll("\t", "    "), (float)0, (float)0+(lineNr*font.lineHeight), color);
            lineNr++;
        }
        poseStack.popPose();
    }

    public void renderBezels(final @NotNull PoseStack pose, final int mouseX, final int mouseY, float partialTicks) {
        tasks.clear();

        this.renderBackground(pose);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, LAPTOP_GUI);

        //*************************//
        //     Physical Screen     //
        //*************************//
        int posX = (width - DEVICE_WIDTH) / 2;
        int posY = (height - DEVICE_HEIGHT) / 2;

        // Corners
        blit(pose, posX, posY, 0, 0, BORDER, BORDER); // TOP-LEFT
        blit(pose, posX + DEVICE_WIDTH - BORDER, posY, 11, 0, BORDER, BORDER); // TOP-RIGHT
        blit(pose, posX + DEVICE_WIDTH - BORDER, posY + DEVICE_HEIGHT - BORDER, 11, 11, BORDER, BORDER); // BOTTOM-RIGHT
        blit(pose, posX, posY + DEVICE_HEIGHT - BORDER, 0, 11, BORDER, BORDER); // BOTTOM-LEFT

        // Edges
        Gui.blit(pose, posX + BORDER, posY, SCREEN_WIDTH, BORDER, 10, 0, 1, BORDER, 256, 256); // TOP
        Gui.blit(pose, posX + DEVICE_WIDTH - BORDER, posY + BORDER, BORDER, SCREEN_HEIGHT, 11, 10, BORDER, 1, 256, 256); // RIGHT
        Gui.blit(pose, posX + BORDER, posY + DEVICE_HEIGHT - BORDER, SCREEN_WIDTH, BORDER, 10, 11, 1, BORDER, 256, 256); // BOTTOM
        Gui.blit(pose, posX, posY + BORDER, BORDER, SCREEN_HEIGHT, 0, 11, BORDER, 1, 256, 256); // LEFT

        // Center
        Gui.blit(pose, posX + BORDER, posY + BORDER, SCREEN_WIDTH, SCREEN_HEIGHT, 10, 10, 1, 1, 256, 256);

    }

    /**
     * Render the laptop screen.
     *
     * @param pose         the pose stack.
     * @param mouseX       the current mouse X position.
     * @param mouseY       the current mouse Y position.
     * @param partialTicks the rendering partial ticks that forge give use (which is useless here).
     */
    public void renderLaptop(final @NotNull PoseStack pose, final int mouseX, final int mouseY, float partialTicks) {
        int posX = (width - DEVICE_WIDTH) / 2;
        int posY = (height - DEVICE_HEIGHT) / 2;
        // Fixes the strange partialTicks that Forge decided to give us
        final float frameTime = Minecraft.getInstance().getFrameTime();
        for (Runnable task : tasks) {
            task.run();
        }
        renderBezels(pose, mouseX, mouseY, partialTicks);
        //*******************//
        //     Wallpaper     //
        //*******************//
        //RenderSystem.setShaderTexture(0, WALLPAPERS.get(currentWallpaper));
        //RenderUtil.drawRectWithTexture(pose, posX + 10, posY + 10, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, 512, 288);
        Image.CACHE.forEach((s, cachedImage) -> cachedImage.delete());
        this.wallpaperLayout.render(pose, this, this.minecraft, posX+10, posY+10, mouseX, mouseY, true, partialTicks);
        boolean insideContext = false;
        if (context != null) {
            insideContext = isMouseInside(mouseX, mouseY, context.xPosition, context.yPosition, context.xPosition + context.width, context.yPosition + context.height);
        }

        //****************//
        //     Window     //
        //****************//
        pose.pushPose();
        {
         //   Window<?>[] windows1 = Arrays.stream(windows.toArray()).filter(Objects::nonNull).toArray(Window<?>[]::new);
            for (int i = windows.size() - 1; i >= 0; i--) {
                var window = windows.get(i);
                if (window != null) {
                    PoseStack.Pose last = pose.last();
                    try {
                        if (i == 0) {
                            window.render(pose, this, minecraft, posX + BORDER, posY + BORDER, mouseX, mouseY, !insideContext, partialTicks);
                        } else {
                            window.render(pose, this, minecraft, posX + BORDER, posY + BORDER, Integer.MAX_VALUE, Integer.MAX_VALUE, false, partialTicks);
                        }
                    } catch (Exception e) {
                        while (pose.last() != last) {
                            pose.popPose();
                        }
                        RenderSystem.disableScissor();
                        e.printStackTrace();
                        Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
                        message.setTitle("Error");
                        CompoundTag intent = new CompoundTag();
                        if (window.content instanceof Application app) {
                            AppInfo info = app.getInfo();
                            if (info != null) {
                                intent.putString("name", info.getName());
                            }
                            openApplication(ApplicationManager.getApplication(Devices.id("diagnostics")), intent);
                            closeApplication(app);
                        }
                    }
                    pose.translate(0, 0, 100);
                }
            }
        }
        pose.popPose();

        //****************************//
        // Render the Application Bar //
        //****************************//
        bar.render(pose, this, minecraft, posX + 10, posY + DEVICE_HEIGHT - 28, mouseX, mouseY, frameTime);

        if (context != null) {
            context.render(pose, this, minecraft, context.xPosition, context.yPosition, mouseX, mouseY, true, frameTime);
        }

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

        super.render(pose, mouseX, mouseY, frameTime);

        GLHelper.clearScissorStack();
    }

    private boolean isMouseInside(int mouseX, int mouseY, int startX, int startY, int endX, int endY) {
        return mouseX >= startX && mouseX <= endX && mouseY >= startY && mouseY <= endY;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        try {
            return mouseClickedInternal(mouseX, mouseY, mouseButton);
        } catch (NullPointerException e) {
            bsod(e);// null
        } catch (Exception e) {
            bsod(e);
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    private void bsod(Throwable e) {
        this.bsod = new BSOD(e);
        e.printStackTrace();
    }
    private static final class BSOD {
        private final Throwable throwable;
        public BSOD(Throwable e) {
            this.throwable = e;
        }
    }
    @SuppressWarnings("unchecked")
    public boolean mouseClickedInternal(double mouseX, double mouseY, int mouseButton) {
        this.lastMouseX = (int) mouseX;
        this.lastMouseY = (int) mouseY;

        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;

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

        this.bar.handleClick(this, posX, posY + SCREEN_HEIGHT - TaskBar.BAR_HEIGHT, (int) mouseX, (int) mouseY, mouseButton);

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

                        windows.get(0).handleMouseClick(this, posX, posY, (int) mouseX, (int) mouseY, mouseButton);

                        if (isMouseWithinWindowBar((int) mouseX, (int) mouseY, dialogWindow)) {
                            this.dragging = true;
                            return false;
                        }

                        if (isMouseWithinWindowBar((int) mouseX, (int) mouseY, window) && dialogWindow == null) {
                            this.dragging = true;
                            return false;
                        }
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
                    message.setTitle("Error");
                    if (windows.size() == 0 || windows.get(0) == null) {
                        CompoundTag intent = new CompoundTag();
                        AppInfo info = window.content.getInfo();
                        if (info != null) {
                            intent.putString("name", info.getName());
                        }
                        openApplication(ApplicationManager.getApplication(Devices.id("diagnostics")), intent);
                    } else {
                        windows.get(0).openDialog(message);
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
            e.printStackTrace();
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.get(0).openDialog(message);
        }
        return true;
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
        boolean override = super.charTyped(codePoint, modifiers);
        try {
            if (!override && windows.get(0) != null)
                windows.get(0).handleCharTyped(codePoint, modifiers);
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.get(0).openDialog(message);
        }
        return override;
    }

    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        final boolean override = super.keyPressed(keyCode, scanCode, modifiers);

        try {
            if (!pressed.contains(keyCode) && !override && windows.get(0) != null) {
                windows.get(0).handleKeyPressed(keyCode, scanCode, modifiers);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.get(0).openDialog(message);
        }
        pressed.add(keyCode);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        pressed.remove(keyCode);

        boolean b = super.keyReleased(keyCode, scanCode, modifiers);

        try {
            if (keyCode >= 32 && keyCode < 256 && windows.get(0) != null) {
                windows.get(0).handleKeyReleased(keyCode, scanCode, modifiers);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.get(0).openDialog(message);
        }
        return b;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;

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
                    if (isMouseOnScreen((int) mouseX, (int) mouseY)) {
                        Objects.requireNonNullElse(dialogWindow, window).handleWindowMove(posX, posY, (int) -(lastMouseX - mouseX), (int) -(lastMouseY - mouseY));
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
            e.printStackTrace();
            Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
            message.setTitle("Error");
            windows.get(0).openDialog(message);
        }
        this.lastMouseX = (int) mouseX;
        this.lastMouseY = (int) mouseY;
        return true;
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta != 0) {
            try {
                if (windows.get(0) != null) {
                    windows.get(0).handleMouseScroll((int) mouseX, (int) mouseY, delta, delta >= 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
                message.setTitle("Error");
                windows.get(0).openDialog(message);
            }
        }
        return true;
    }

    @Override
    public void renderComponentTooltip(@NotNull PoseStack pose, @NotNull List<Component> tooltips, int x, int y) {
        super.renderComponentTooltip(pose, tooltips, x, y);
    }

    public Pair<Application, Boolean> sendApplicationToFront(AppInfo info) {
        int i = 0;
        for (; i < windows.size(); i++) {
            Window<?> window = windows.get(i);
            if (window != null && window.content instanceof Application && ((Application) window.content).getInfo() == info) {
                windows.remove(i);
                updateWindowStack();
                windows.add(0, window);
                i--;
                return Pair.of((Application) window.content, true);
            }
        }
        return Pair.of(null, false);
    }

    @Override
    public Application openApplication(AppInfo info) {
        return openApplication(info, (CompoundTag) null);
    }

    @Override
    public Application openApplication(AppInfo info, CompoundTag intentTag) {
        Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
        Application[] a = new Application[]{null};
        optional.ifPresent(application -> a[0] = openApplication(application, intentTag));
        return a[0];
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
            if (q.right())
                return q.left();

            if (app instanceof SystemApp) {
                ((SystemApp) app).setLaptop(this);
            }

            if (app instanceof SystemAccessor) {
                ((SystemAccessor) app).sendSystem(this);
            }

            Window<Application> window = new Window<>(app, this);
            window.init((width - SCREEN_WIDTH) / 2, (height - SCREEN_HEIGHT) / 2, intent);

            if (appData.contains(app.getInfo().getFormattedId())) {
                app.load(appData.getCompound(app.getInfo().getFormattedId()));
            }

            if (app.getCurrentLayout() == null) {
                app.restoreDefaultLayout();
            }

            addWindow(window);

            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
        } catch (Exception e) {
            e.printStackTrace();
            AppInfo info = ApplicationManager.getApplication(Devices.id("diagnostics"));
            system.openApplication(info);
        }
        return app;
    }

    @Override
    public Pair<Application, Boolean> openApplication(AppInfo info, File file) {
        if (isApplicationNotInstalled(info))
            return Pair.of(null, false);

        if (isInvalidApplication(info))
            return Pair.of(null, false);

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
                        return Pair.of(application, false);
                    }
                    return Pair.of(application, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppInfo info1 = ApplicationManager.getApplication(Devices.id("diagnostics"));
            system.openApplication(info1);
        }
        return Pair.of(null, true);
    }

    @Override
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
                        ((SystemApp) app).setLaptop(null);
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
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;
        return isMouseInside(mouseX, mouseY, posX, posY, posX + SCREEN_WIDTH, posY + SCREEN_HEIGHT);
    }

    private boolean isMouseWithinWindowBar(int mouseX, int mouseY, Window<?> window) {
        if (window == null) return false;
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX + 1, posY + window.offsetY + 1, posX + window.offsetX + window.width - 13, posY + window.offsetY + 11);
    }

    private boolean isMouseWithinWindow(int mouseX, int mouseY, Window<?> window) {
        if (window == null) return false;
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX, posY + window.offsetY, posX + window.offsetX + window.width, posY + window.offsetY + window.height);
    }

    public boolean isMouseWithinApp(int mouseX, int mouseY, Window<?> window) {
        int posX = (width - SCREEN_WIDTH) / 2;
        int posY = (height - SCREEN_HEIGHT) / 2;
        return isMouseInside(mouseX, mouseY, posX + window.offsetX + 1, posY + window.offsetY + 13, posX + window.offsetX + window.width - 1, posY + window.offsetY + window.height - 1);
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
            this.currentWallpaper = new Wallpaper(currentWallpaper.location+1);
        }
        wallpaperUpdated();
    }

    public void prevWallpaper() {
        if (currentWallpaper.location - 1 >= 0) {
            this.currentWallpaper = new Wallpaper(currentWallpaper.location-1);
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
        return ImmutableList.copyOf(WALLPAPERS);
    }

    @Nullable
    public Application getApplication(String appId) {
        return APPLICATIONS.stream().filter(app -> app.getInfo().getFormattedId().equals(appId)).findFirst().orElse(null);
    }

    @Override
    public List<AppInfo> getInstalledApplications() {
        return ImmutableList.copyOf(installedApps);
    }

    public boolean isApplicationInstalled(AppInfo info) {
        return info.isSystemApp() || installedApps.contains(info);
    }

    public boolean isApplicationNotInstalled(AppInfo info) {
        return !isApplicationInstalled(info);
    }

    private boolean isValidApplication(AppInfo info) {
        if (Devices.hasAllowedApplications()) {
            return Devices.getAllowedApplications().contains(info);
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

    public Settings getSettings() {
        return settings;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void openContext(Layout layout, int x, int y) {
        layout.updateComponents(x, y);
        context = layout;
        layout.init();
    }

    @Override
    public boolean hasContext() {
        return context != null;
    }

    @Override
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
            var a = tag.getString("url");
            var b = tag.getInt("location");
            if (tag.contains("url", 8)) {
                this.url = a;
                this.location = -87;
            } else {
                this.url = null;
                this.location = b;
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
