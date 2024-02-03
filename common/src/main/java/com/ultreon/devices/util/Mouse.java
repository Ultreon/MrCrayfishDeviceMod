package com.ultreon.devices.util;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Mouse {
    public static int[] getEventPos() {
        double[] xPos = new double[1];
        double[] yPos = new double[1];
        GLFW.glfwGetCursorPos(Minecraft.getInstance().getWindow().getWindow(), xPos, yPos);
        return new int[]{(int) xPos[0], (int) yPos[0]};
    }

    public static int[] getScaledEventPos() {
        double[] xPos = new double[1];
        double[] yPos = new double[1];
        GLFW.glfwGetCursorPos(Minecraft.getInstance().getWindow().getWindow(), xPos, yPos);
        return new int[]{(int) (xPos[0] / Minecraft.getInstance().getWindow().getGuiScale()), (int) (yPos[0] / Minecraft.getInstance().getWindow().getGuiScale())};
    }

    public static int getEventX() {
        return getEventPos()[0];
    }

    public static int getEventY() {
        return getEventPos()[1];
    }

    public static int getScaledEventX() {
        return getScaledEventPos()[0];
    }

    public static int getScaledEventY() {
        return getScaledEventPos()[1];
    }
}
