package com.ultreon.devices.forge;

import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import static com.ultreon.devices.Devices.MOD_ID;

public class ReferenceImpl {
    public static String getVersion() {
        ModContainer container = ModList.get().getModContainerById(MOD_ID).orElseThrow();
        IModInfo info = container.getModInfo();
        return MavenVersionStringHelper.artifactVersionToString(info.getVersion());
    }
}
