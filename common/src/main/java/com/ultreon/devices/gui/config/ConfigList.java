package com.ultreon.devices.gui.config;

import io.github.xypercode.craftyconfig.CraftyConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigList extends ContainerObjectSelectionList<ConfigList.ListEntry> {
    private final List<ListEntry> entries = new ArrayList<>();
    private CraftyConfig config;

    public ConfigList(Minecraft minecraft, int width, int height, int i, int i1, CraftyConfig config) {
        super(minecraft, width, height, i, i1, 28);
        this.config = config;
        this.centerListVertically = false;
    }

    public void addEntries(ConfigData[] options) {
        for (ConfigData option : options) {
            ListEntry of = ListEntry.of(this, this.getRowWidth(), option);
            this.entries.add(of);
            this.addEntry(of);
        }
    }

    @Override
    protected void clearEntries() {
        super.clearEntries();
        this.entries.clear();
    }

    public int getRowWidth() {
        return this.width - 4;
    }

    protected int getScrollbarPosition() {
        return this.width - 5;
    }

    public void save() {
        for (ListEntry entry : this.entries) {
            entry.configEntry.setFromWidget(entry.widget);
        }
        config.save();
    }

    protected static class ListEntry extends Entry<ListEntry> {
        private final ConfigList list;
        final ConfigData configEntry;
        final AbstractWidget widget;

        private ListEntry(ConfigList list, ConfigData configEntry, AbstractWidget widget) {
            this.list = list;
            this.configEntry = configEntry;
            this.widget = widget;
        }

        public static ListEntry of(ConfigList list, int rowWidth, ConfigData rightOption) {
            return new ListEntry(list, rightOption, rightOption.createButton(rowWidth - 160, 0, 150));
        }

        public void render(GuiGraphics gfx, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean selected, float partialTicks) {
            if (this.list.isMouseOver(mouseX, mouseY) && this.isMouseOver(mouseX, mouseY)) {
                gfx.fill(x - 4, y, x + rowWidth, y + rowHeight, 0x40ffffff);
            }

            Minecraft mc = Minecraft.getInstance();
            gfx.drawString(mc.font, this.configEntry.getDescription(), 2 + x, y + rowHeight / 2 - mc.font.lineHeight / 2, 0xffffffff, true);

            this.widget.setY(y + 2);
            this.widget.render(gfx, mouseX, mouseY, partialTicks);
        }

        @NotNull
        public List<? extends GuiEventListener> children() {
            return Collections.singletonList(this.widget);
        }

        @NotNull
        public List<? extends NarratableEntry> narratables() {
            return Collections.singletonList(this.widget);
        }
    }
}
