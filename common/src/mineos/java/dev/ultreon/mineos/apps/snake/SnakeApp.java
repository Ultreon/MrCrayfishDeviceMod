package dev.ultreon.mineos.apps.snake;

import dev.ultreon.devices.impl.app.Application;
import dev.ultreon.devices.impl.app.Layout;
import dev.ultreon.devices.impl.app.component.Button;
import dev.ultreon.devices.impl.app.component.Label;
import dev.ultreon.mineos.apps.snake.layout.SnakeLayout;
import dev.ultreon.mineos.userspace.MineOS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class SnakeApp extends Application {
    public Layout titleScreen;
    public SnakeLayout gameLayout;
    @Override
    public void init(@Nullable CompoundTag intent) {
        this.titleScreen = new Layout(200, 100);
        var startButton = new Button(30, 70, "Start");
        startButton.setClickListener(((mouseX, mouseY, mouseButton) -> {
            this.gameLayout = new SnakeLayout(this);
            this.setCurrentLayout(this.gameLayout);
        }));
        var titleText = new Label("Snake", 10, 10);
        titleText.setScale(2);

        titleScreen.addComponent(titleText);
        titleScreen.addComponent(startButton);
        setCurrentLayout(titleScreen);
    }

    @Override
    public void render(GuiGraphics graphics, MineOS laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {
        super.render(graphics, laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
//        for (Component component : this.titleScreen.components) {
//            DebugLog.log(component + "lt: " + component.left + ", " + component.top);
//        }
    }

    @Override
    public void load(CompoundTag tag) {

    }

    @Override
    public void save(CompoundTag tag) {

    }
}
