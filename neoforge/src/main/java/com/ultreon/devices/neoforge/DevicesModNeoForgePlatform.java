package com.ultreon.devices.neoforge;

import com.ultreon.devices.DevicesModPlatform;
import net.neoforged.fml.ModList;
import net.neoforged.fml.i18n.MavenVersionTranslator;

public class DevicesModNeoForgePlatform implements DevicesModPlatform {
    @Override
    public String getVersion() {
        return MavenVersionTranslator.artifactVersionToString(ModList.get().getModContainerById("devices").orElseThrow().getModInfo().getVersion());
    }
}
