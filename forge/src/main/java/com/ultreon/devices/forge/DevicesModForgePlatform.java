package com.ultreon.devices.forge;

import com.ultreon.devices.DevicesModPlatform;
import net.minecraftforge.common.util.MavenVersionStringHelper;
import net.minecraftforge.fml.ModList;

public class DevicesModForgePlatform implements DevicesModPlatform {
    @Override
    public String getVersion() {
        return MavenVersionStringHelper.artifactVersionToString(ModList.get().getModContainerById("devices").orElseThrow().getModInfo().getVersion());
    }
}
