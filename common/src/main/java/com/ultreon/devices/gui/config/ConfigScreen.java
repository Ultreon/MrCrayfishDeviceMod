package com.ultreon.devices.gui.config;

import io.github.xypercode.craftyconfig.CraftyConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigScreen extends Screen {
    private final Screen back;
    private ConfigList list;
    private Button doneButton;
    private Button cancelButton;
    private final CraftyConfig config;

    public ConfigScreen(CraftyConfig config, Screen back) {
        super(Component.translatable("screen.devices.config"));
        this.config = config;
        this.back = back;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        super.init();

        this.list = new ConfigList(this.minecraft, this.width, this.height, 32, this.height - 32, this.config);
        this.list.addEntries(this.loadEntries());
        this.addRenderableWidget(this.list);

        this.doneButton = new Button.Builder(CommonComponents.GUI_DONE, button -> {
            this.list.save();
            assert this.minecraft != null;
            this.minecraft.setScreen(this.back);
        }).bounds(this.width / 2 + 5, this.height - 6 - 20, 150, 20).build();
        this.addRenderableWidget(this.doneButton);

        this.cancelButton = new Button.Builder(CommonComponents.GUI_CANCEL, button -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(this.back);
        }).bounds(this.width / 2 - 155, this.height - 6 - 20, 150, 20).build();
        this.addRenderableWidget(this.cancelButton);
    }

    private ConfigData[] loadEntries() {
        List<ConfigData> list = new ArrayList<>();
        Map<String, Object> all = config.getAll();
        Set<String> strings = all.keySet();
        List<String> keys = new ArrayList<>(strings);
        keys.sort((String::compareTo));
        for (String key : strings) {
            Object currentValue = all.get(key);
            Class<?> type = config.getType(key);

            list.add(new ConfigData(key, currentValue, type, config.getDefault(key), config.getRange(key)) {
                @Override
                public Object getCurrentValue() {
                    return config.get(key);
                }

                @Override
                public void setCurrentValue(Object value) {
                    config.set(key, value);
                    config.save();
                }
            });
        }
        return list.toArray(ConfigData[]::new);
    }

    @Override
    public void render(GuiGraphics gfx, int i, int j, float f) {
        super.render(gfx, i, j, f);

        gfx.drawCenteredString(this.font, this.getTitle(), this.width / 2, 16 - this.font.lineHeight / 2, 0xffffffff);
    }

    public Screen getBack() {
        return this.back;
    }

    public ConfigList getList() {
        return this.list;
    }

    public Button getDoneButton() {
        return this.doneButton;
    }
}
