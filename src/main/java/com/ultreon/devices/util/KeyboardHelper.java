package com.ultreon.devices.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public class KeyboardHelper {
    public static boolean isKeyDown(int key) {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
    }

    public static boolean isCtrlDown() {
        return isKeyDown(GLFW.GLFW_KEY_LCONTROL) || isKeyDown(GLFW.GLFW_KEY_RCONTROL);
    }

    public static boolean isShiftDown() {
        return isKeyDown(GLFW.GLFW_KEY_LSHIFT) || isKeyDown(GLFW.GLFW_KEY_RSHIFT);
    }

    public static boolean isAltDown() {
        return isKeyDown(GLFW.GLFW_KEY_LALT) || isKeyDown(GLFW.GLFW_KEY_RALT);
    }

    public static boolean isWinDown() {
        return isKeyDown(GLFW.GLFW_KEY_LWIN) || isKeyDown(GLFW.GLFW_KEY_RWIN);
    }
}
