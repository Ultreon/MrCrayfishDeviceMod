package com.ultreon.devices;

import net.minecraftforge.fml.MavenVersionStringHelper;
import net.minecraftforge.fml.ModList;

import java.util.NoSuchElementException;

public class Reference {
    public static final String MOD_ID = "devices";
    public static final String VERSION;

    static {
        VERSION = getVersion();
    }

    public static String getVersion() {
        return MavenVersionStringHelper.artifactVersionToString(ModList.get().getModContainerById(MOD_ID).orElseThrow(NoSuchElementException::new).getModInfo().getVersion());
    }
}
