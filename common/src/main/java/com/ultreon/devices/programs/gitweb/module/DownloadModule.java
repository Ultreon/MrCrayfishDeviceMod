package com.ultreon.devices.programs.gitweb.module;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Dialog;
import com.ultreon.devices.api.app.Icons;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.app.component.Button;
import com.ultreon.devices.api.io.File;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.gitweb.component.GitWebFrame;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBTParser;

import java.awt.*;
import java.util.Map;

/**
 * @author MrCrayfish
 */
public class DownloadModule extends Module {
    @Override
    public String[] getRequiredData() {
        return new String[]{"file-app", "file-data"};
    }

    @Override
    public String[] getOptionalData() {
        return new String[]{"file-name", "text"};
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width) {
        return 45;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
        int height = calculateHeight(data, width) - 5;
        AppInfo info = ApplicationManager.getApplication(data.get("file-app"));
        layout.setBackground((pose, gui, mc, x, y, width1, height1, mouseX, mouseY, windowActive) -> {
            int section = layout.width / 6;
            int subWidth = section * 4;
            int posX = x + section;
            int posY = y + 5;
            IngameGui.fill(pose, posX, posY, posX + subWidth, posY + height - 5, Color.BLACK.getRGB());
            IngameGui.fill(pose, posX + 1, posY + 1, posX + subWidth - 1, posY + height - 5 - 1, Color.DARK_GRAY.getRGB());

            RenderSystem.blendColor(1f, 1f, 1f, 1f);
            mc.textureManager.bind(Laptop.ICON_TEXTURES);
//            int iconU = 0, iconV = 0;
//            if (info != null) {
//                iconU = info.getIconU();
//                iconV = info.getIconV();
//            }

            RenderUtil.drawIcon(pose, posX + 5, posY + 3, info, 28, 28);

      //      RenderUtil.drawRectWithTexture(pose, posX + 5, posY + 3, iconU, iconV, 28, 28, 14, 14, 224, 224);

            int textWidth = subWidth - 70 - 10 - 30 - 5;
            RenderUtil.drawStringClipped(pose, data.getOrDefault("file-name", "File"), posX + 37, posY + 7, textWidth, Color.ORANGE.getRGB(), true);
            if (data.containsKey("text")) {
                RenderUtil.drawStringClipped(pose, data.get("text"), posX + 37, posY + 19, textWidth, Color.LIGHT_GRAY.getRGB(), false);
            }
        });

        int section = layout.width / 6;
        Button button = new Button(0, 10, "Download", Icons.IMPORT);
        button.left = section * 5 - 70 - 5;
        button.setSize(70, height - 15);
        button.setClickListener((mouseX, mouseY, mouseButton) -> {
            try {
                CompoundNBT tag = TagParser.parseTag(data.get("file-data"));
                File file = new File(data.getOrDefault("file-name", ""), data.get("file-app"), tag);
                Dialog dialog = new Dialog.SaveFile(frame.getApp(), file);
                frame.getApp().openDialog(dialog);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        });
        layout.addComponent(button);
    }
}
