package com.ultreon.devices.programs.snake;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.app.component.Label;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.programs.snake.layout.SnakeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;

public class SnakeApp extends Application {
    public Layout titleScreen;
    public SnakeLayout gameLayout;
    @Override
    public void init(@Nullable CompoundNBT intent) {
        this.titleScreen = new Layout(200, 100);
        Button startButton = new Button(30, 70, "Start");
        startButton.setClickListener(((mouseX, mouseY, mouseButton) -> {
            this.gameLayout = new SnakeLayout(this);
            this.setCurrentLayout(this.gameLayout);
        }));
        Label titleText = new Label("Snake", 10, 10);
        titleText.setScale(2);

        titleScreen.addComponent(titleText);
        titleScreen.addComponent(startButton);
        setCurrentLayout(titleScreen);
    }

    @Override
    public void render(MatrixStack pose, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {
        super.render(pose, laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
//        for (Component component : this.titleScreen.components) {
//            System.out.println(component + "lt: " + component.left + ", " + component.top);
//        }
    }

    @Override
    public void load(CompoundNBT tag) {

    }

    @Override
    public void save(CompoundNBT tag) {

    }
}
