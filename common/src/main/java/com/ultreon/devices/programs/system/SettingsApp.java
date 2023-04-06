package com.ultreon.devices.programs.system;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.CheckBox;
import com.ultreon.devices.api.app.component.ComboBox;
import com.ultreon.devices.api.app.renderer.ItemRenderer;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.Settings;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.object.TrayItem;
import com.ultreon.devices.programs.system.component.Palette;
import com.ultreon.devices.programs.system.object.ColorScheme;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Objects;
import java.util.Stack;

@SuppressWarnings("FieldCanBeLocal")
public class SettingsApp extends SystemApp {
    @Environment(EnvType.CLIENT)
    private Button backBtn;

    @Environment(EnvType.CLIENT)
    private Layout layoutMain;
    @Environment(EnvType.CLIENT)
    private Layout layoutGeneral;
    @Environment(EnvType.CLIENT)
    private CheckBox checkBoxShowApps;

    @Environment(EnvType.CLIENT)
    private Layout layoutPersonalise;
    @Environment(EnvType.CLIENT)
    private Layout layoutWallpaper;
    @Environment(EnvType.CLIENT)
    private Button prevWallpaperBtn;
    @Environment(EnvType.CLIENT)
    private Button nextWallpaperBtn;
    @Environment(EnvType.CLIENT)
    private Button urlWallpaperBtn;

    @Environment(EnvType.CLIENT)
    private Layout layoutColorScheme;
    @Environment(EnvType.CLIENT)
    private Button buttonColorSchemeApply;

    @Environment(EnvType.CLIENT)
    private Stack<Layout> predecessor;

    public SettingsApp() {
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> predecessor = new Stack<>());
    }

    private void resetColorSchemeClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            Laptop.getSystem().getSettings().getColorScheme().resetDefault();
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void init(@Nullable CompoundTag intent) {
        backBtn = new Button(2, 2, Icons.ARROW_LEFT);
        backBtn.setVisible(false);
        backBtn.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                if (predecessor.size() > 0) {
                    setCurrentLayout(predecessor.pop());
                }
                if (predecessor.isEmpty()) {
                    backBtn.setVisible(false);
                }
            }
        });

        layoutMain = addMainLayout();
        setCurrentLayout(layoutMain);
    }

    /**
     * Creates the main layout of the settings app
     *
     * @return the main layout.
     */
    @Environment(EnvType.CLIENT)
    private Menu addMainLayout() {
        Menu layoutMain = new Menu("Home");

        Button buttonColorScheme = new Button(5, 26, "Personalise", Icons.EDIT);
        buttonColorScheme.setSize(90, 20);
        buttonColorScheme.setToolTip("Personalise", "Change the wallpaper, UI colors, and more!");
        buttonColorScheme.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                showMenu(layoutPersonalise);
            }
        });
        layoutMain.addComponent(buttonColorScheme);

        layoutGeneral = new Menu("General");
        layoutGeneral.addComponent(backBtn);

        checkBoxShowApps = new CheckBox("Show All Apps", 5, 5);
        checkBoxShowApps.setSelected(Settings.isShowAllApps());
        checkBoxShowApps.setClickListener(this::showAllAppsClick);
        layoutGeneral.addComponent(checkBoxShowApps);

        layoutPersonalise = createPersonaliseLayout();

        return layoutMain;
    }

    /**
     * Create the layout for personalising the laptop
     *
     * @return the menu layout.
     */
    @Environment(EnvType.CLIENT)
    private Layout createPersonaliseLayout() {
        Layout layoutPersonalise = new Menu("Personalise");
        layoutPersonalise.addComponent(backBtn);

        // Wallpaper button on personalise menu.
        Button buttonWallpaper = new Button(5, 26, "Wallpaper", Icons.EDIT);
        buttonWallpaper.setSize(90, 20);
        //buttonWallpaper.top = this.getHeight()-buttonWallpaper.getHeight()-5;
        buttonWallpaper.setToolTip("Wallpaper", "Manage the wallpaper.");
        buttonWallpaper.setClickListener(this::wallpaperClick);
        layoutPersonalise.addComponent(buttonWallpaper);

        //****************************//
        //     Wallpaper settings     //
        //****************************//
        layoutWallpaper = addWallpaperLayout();

        // Reset color scheme button on personalise menu.
        Button buttonReset = new Button(6, 100, "Reset Color Scheme");
        buttonReset.setClickListener(this::resetColorSchemeClick);
        buttonReset.top = layoutPersonalise.height - buttonReset.getHeight() - 5;
        layoutPersonalise.addComponent(buttonReset);
        layoutPersonalise.addComponent(backBtn);

        //***********************//
        //     Color schemes     //
        //***********************//
        layoutColorScheme = createColorSchemeLayout();

        // Reset color scheme button on personalise menu.
        Button buttonColorScheme = new Button(6, 80, "Color scheme");
        buttonColorScheme.setClickListener(this::colorSchemeClick);
        buttonColorScheme.top = layoutPersonalise.height - buttonColorScheme.getHeight() - 25;
        layoutPersonalise.addComponent(buttonColorScheme);

        return layoutPersonalise;
    }

    /**
     * Create the layout for the color schemes
     *
     * @return the layout.
     */
    @Environment(EnvType.CLIENT)
    private Layout createColorSchemeLayout() {
        final Layout layoutColorScheme = new Menu("UI Colors");
        layoutColorScheme.addComponent(backBtn);

        ComboBox.Custom<Integer> comboBoxTextColor = createColorPicker(145, 26);
        comboBoxTextColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getTextColor());
        layoutColorScheme.addComponent(comboBoxTextColor);

        ComboBox.Custom<Integer> comboBoxTextSecondaryColor = createColorPicker(145, 44);
        comboBoxTextSecondaryColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getTextSecondaryColor());
        layoutColorScheme.addComponent(comboBoxTextSecondaryColor);

        ComboBox.Custom<Integer> comboBoxHeaderColor = createColorPicker(145, 62);
        comboBoxHeaderColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getHeaderColor());
        layoutColorScheme.addComponent(comboBoxHeaderColor);

        ComboBox.Custom<Integer> comboBoxBackgroundColor = createColorPicker(145, 80);
        comboBoxBackgroundColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getBackgroundColor());
        layoutColorScheme.addComponent(comboBoxBackgroundColor);

        ComboBox.Custom<Integer> comboBoxBackgroundSecondaryColor = createColorPicker(145, 98);
        comboBoxBackgroundSecondaryColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getBackgroundSecondaryColor());
        layoutColorScheme.addComponent(comboBoxBackgroundSecondaryColor);

        ComboBox.Custom<Integer> comboBoxItemBackgroundColor = createColorPicker(145, 116);
        comboBoxItemBackgroundColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
        layoutColorScheme.addComponent(comboBoxItemBackgroundColor);

        ComboBox.Custom<Integer> comboBoxItemHighlightColor = createColorPicker(145, 134);
        comboBoxItemHighlightColor.setValue(Laptop.getSystem().getSettings().getColorScheme().getItemHighlightColor());
        layoutColorScheme.addComponent(comboBoxItemHighlightColor);

        buttonColorSchemeApply = new Button(5, 79, Icons.CHECK);
        buttonColorSchemeApply.setEnabled(false);
        buttonColorSchemeApply.setToolTip("Apply", "Set these colors as the new color scheme");
        buttonColorSchemeApply.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            if (mouseButton == 0) {
                ColorScheme colorScheme = Laptop.getSystem().getSettings().getColorScheme();
                colorScheme.setTextColor(comboBoxTextColor.getValue());
                colorScheme.setTextSecondaryColor(comboBoxTextSecondaryColor.getValue());
                colorScheme.setHeaderColor(comboBoxHeaderColor.getValue());
                colorScheme.setBackgroundColor(comboBoxBackgroundColor.getValue());
                colorScheme.setBackgroundSecondaryColor(comboBoxBackgroundSecondaryColor.getValue());
                colorScheme.setItemBackgroundColor(comboBoxItemBackgroundColor.getValue());
                colorScheme.setItemHighlightColor(comboBoxItemHighlightColor.getValue());
                buttonColorSchemeApply.setEnabled(false);
            }
        });
        layoutColorScheme.addComponent(buttonColorSchemeApply);

        return layoutColorScheme;
    }

    /**
     * Create the layout for the wallpaper settings
     *
     * @return the layout.
     */
    @Environment(EnvType.CLIENT)
    private Layout addWallpaperLayout() {
        // Create layout.
        Layout wallpaperLayout = new Menu("Wallpaper");

        // Wallpaper image.
        var image = new com.ultreon.devices.api.app.component.Image(6, 29, 6+122, 29+70);
        image.setBorderThickness(1);
        image.setBorderVisible(true);
        image.setImage(Objects.requireNonNull(getLaptop()).getCurrentWallpaper());
        wallpaperLayout.addComponent(image);

        // Previous wallpaper button.
        prevWallpaperBtn = new Button(135, 27, Icons.ARROW_LEFT);
        prevWallpaperBtn.setSize(25, 20);
        prevWallpaperBtn.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton != 0)
                return;

            Laptop laptop = getLaptop();
            if (laptop != null) {
                laptop.prevWallpaper();
                image.setImage(getLaptop().getCurrentWallpaper());
            }
        });
        prevWallpaperBtn.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
        wallpaperLayout.addComponent(prevWallpaperBtn);

        // Next wallpaper button.
        nextWallpaperBtn = new Button(165, 27, Icons.ARROW_RIGHT);
        nextWallpaperBtn.setSize(25, 20);
        nextWallpaperBtn.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton != 0)
                return;

            Laptop laptop = getLaptop();
            if (laptop != null) {
                laptop.nextWallpaper();
                image.setImage(getLaptop().getCurrentWallpaper());
            }
        });
        nextWallpaperBtn.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
        wallpaperLayout.addComponent(nextWallpaperBtn);

        // Reset wallpaper button.
        Button resetWallpaperBtn = new Button(6, 100, "Reset Wallpaper");
        resetWallpaperBtn.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton == 0) {
                getLaptop().setWallpaper(0);
                image.setImage(getLaptop().getCurrentWallpaper());
                prevWallpaperBtn.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
                nextWallpaperBtn.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
            }
        });
        resetWallpaperBtn.top = wallpaperLayout.height - resetWallpaperBtn.getHeight() - 5;
        wallpaperLayout.addComponent(resetWallpaperBtn);

        // Add back button.
        wallpaperLayout.addComponent(backBtn);

        // Add wallpaper load from url button.
        urlWallpaperBtn = new Button(135, 52, "Load", Icons.EARTH);
        urlWallpaperBtn.setSize(55, 20);
        urlWallpaperBtn.setClickListener((mouseX, mouseY, mouseButton) -> {
            if (mouseButton != 0)
                return;

            Dialog.Input dialog = new Dialog.Input("Enter the URL of the image");
            dialog.setResponseHandler((success, string) -> {
                if (getLaptop() != null) {
                    getLaptop().setWallpaper(string);
                    image.setImage(getLaptop().getCurrentWallpaper());
                    prevWallpaperBtn.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
                    nextWallpaperBtn.setEnabled(getLaptop().getCurrentWallpaper().isBuiltIn());
                }
                return success;
            });
            openDialog(dialog);
        });
        wallpaperLayout.addComponent(urlWallpaperBtn);

        return wallpaperLayout;
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }

    private void showMenu(Layout layout) {
        predecessor.push(getCurrentLayout());
        backBtn.setVisible(true);
        setCurrentLayout(layout);
    }

    @Override
    public void onClose() {
        super.onClose();
        predecessor.clear();
    }

    @Environment(EnvType.CLIENT)
    private void wallpaperClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            showMenu(layoutWallpaper);
        }
    }

    @Environment(EnvType.CLIENT)
    private void colorSchemeClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            showMenu(layoutColorScheme);
        }
    }

    @Environment(EnvType.CLIENT)
    private void showAllAppsClick(int mouseX, int mouseY, int mouseButton) {
        Settings.setShowAllApps(checkBoxShowApps.isSelected());
        Laptop laptop = getLaptop();
        assert laptop != null;
        laptop.getTaskBar().setupApplications(laptop.getApplications());
    }

    @Environment(EnvType.CLIENT)
    public static class Menu extends Layout {
        private final String title;

        public Menu(String title) {
            super(200, 150);
            this.title = title;
        }

        @Override
        public void render(PoseStack pose, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
            Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getHeaderColor());
            Gui.fill(pose, x, y, x + width, y + 20, color.getRGB());
            Gui.fill(pose, x, y + 20, x + width, y + 21, color.darker().getRGB());
            mc.font.drawShadow(pose, title, x + 22, y + 6, Color.WHITE.getRGB());
            super.render(pose, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
        }
    }

    @Environment(EnvType.CLIENT)
    public ComboBox.Custom<Integer> createColorPicker(int left, int top) {
        ComboBox.Custom<Integer> colorPicker = new ComboBox.Custom<>(left, top, 50, 100, 100);
        colorPicker.setValue(Color.RED.getRGB());
        colorPicker.setItemRenderer(new ItemRenderer<>() {
            @Override
            public void render(PoseStack pose, Integer integer, GuiComponent gui, Minecraft mc, int x, int y, int width, int height) {
                if (integer != null) {
                    Gui.fill(pose, x, y, x + width, y + height, integer);
                }
            }
        });
        colorPicker.setChangeListener((oldValue, newValue) ->
        {if (buttonColorSchemeApply != null) buttonColorSchemeApply.setEnabled(true);});

        Palette palette = new Palette(5, 5, colorPicker);
        Layout layout = colorPicker.getLayout();
        layout.addComponent(palette);

        return colorPicker;
    }

    public static class SettingsTrayItem extends TrayItem {
        public SettingsTrayItem() {
            super(Icons.WRENCH);
        }

        @Override
        public void handleClick(int mouseX, int mouseY, int mouseButton) {
            AppInfo info = ApplicationManager.getApplication("devices:settings");
            if (info != null) {
                Laptop.getSystem().openApplication(info);
            }
        }
    }
}
