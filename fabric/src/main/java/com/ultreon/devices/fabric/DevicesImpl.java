package com.ultreon.devices.fabric;

import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.print.PrintingManager;
import com.ultreon.devices.core.Laptop;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DevicesImpl {
    public static List<Application> getAPPLICATIONS() {
        return Laptop.getApplicationsForFabric();
    }

    public static Map<String, IPrint.Renderer> getRegisteredRenders(){
        return PrintingManager.getRegisteredRenders();
    }

    public static void setRegisteredRenders(Map<String, IPrint.Renderer> map){
        PrintingManager.setRegisteredRenders(map);
    }

    public static void registerApplicationEvent() {
        var eve = FabricLoader.getInstance().getEntrypointContainers("devices:application_registration", FabricApplicationRegistration.class);
        EntrypointContainer<FabricApplicationRegistration> builtin = null;
        for (EntrypointContainer<FabricApplicationRegistration> fabricApplicationRegistrationEntrypointContainer : eve) {
            if (fabricApplicationRegistrationEntrypointContainer.getProvider().getMetadata().getId().equals("devices")) {
                builtin = fabricApplicationRegistrationEntrypointContainer;
            }
        }
        assert builtin != null;
        builtin.getEntrypoint().registerApplications();
        (eve = new ArrayList<>(eve)).remove(builtin);
        for (EntrypointContainer<FabricApplicationRegistration> fabricApplicationRegistrationEntrypointContainer : eve) {
            fabricApplicationRegistrationEntrypointContainer.getEntrypoint().registerApplications();
        }
    }
}
