package com.ultreon.devices.forge;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.print.PrintingManager;
import com.ultreon.devices.core.Laptop;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.util.List;
import java.util.Map;

public class DevicesImpl {
    public static List<Application> getAPPLICATIONS() {
        return ObfuscationReflectionHelper.getPrivateValue(Laptop.class, null, "APPLICATIONS");
    }

    public static Map<String, IPrint.Renderer> getRegisteredRenders(){
        return ObfuscationReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
    }

    public static void setRegisteredRenders(Map<String, IPrint.Renderer> map){
        ObfuscationReflectionHelper.setPrivateValue(PrintingManager.class, null, map, "registeredRenders");
    }

    public static void registerApplicationEvent() {
        DevicesForge.MOD_EVENTBUS.post(new ForgeApplicationRegistration());
    }
}
