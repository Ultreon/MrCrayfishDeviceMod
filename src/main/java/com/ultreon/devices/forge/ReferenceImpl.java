package com.ultreon.devices.forge;
import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.NoSuchElementException;

import static com.ultreon.devices.Devices.MOD_ID;

public class ReferenceImpl {
    public static String getVersion() {
        ModContainer container = ModList.get().getModContainerById(MOD_ID).orElseThrow(NoSuchElementException::new);
        IModInfo info = container.getModInfo();
        return MavenVersionStringHelper.artifactVersionToString(info.getVersion());
    }
}
