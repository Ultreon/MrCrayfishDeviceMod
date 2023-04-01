package com.ultreon.devices.forge;

import com.mojang.logging.LogUtils;
import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import com.ultreon.devices.LaunchException;
import com.ultreon.devices.Reference;
import com.ultreon.devices.event.WorldDataHandler;
import com.ultreon.devices.init.RegistrationHandler;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.regex.Pattern;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public class DevicesForge {
    private static final Pattern DEV_PREVIEW_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+-dev\\d+");
    private static final boolean IS_DEV_PREVIEW = DEV_PREVIEW_PATTERN.matcher(Reference.VERSION).matches();

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final boolean DEVELOPER_MODE = false;

    public static IEventBus MOD_EVENTBUS;

    public DevicesForge() throws LaunchException {
        EventBuses.registerModEventBus(Devices.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_EVENTBUS.register(BuiltinAppsRegistration.class);
        Devices.preInit();
        FMLJavaModLoadingContext javaFmlLoadingCtx = FMLJavaModLoadingContext.get();
        ModLoadingContext loadingCtx = ModLoadingContext.get();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        IEventBus modEventBus = javaFmlLoadingCtx.getModEventBus();

        // Common side stuff
        forgeEventBus.register(this);
        forgeEventBus.register(new WorldDataHandler());

        LOGGER.info("Registering common setup handler, and load complete handler.");

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::loadComplete);

        LOGGER.info("Initializing registration handler and mod config.");
        RegistrationHandler.register();
        DeviceConfig.register(loadingCtx);

        // Client side stuff
        if (!DatagenModLoader.isRunningDataGen()) {
            LOGGER.info("Registering the reload listener.");
//            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
        }

        // Server side stuff
        LOGGER.info("Registering server setup handler.");
        modEventBus.addListener(this::serverSetup);

        // IMC stuff
        LOGGER.info("Registering IMC handlers.");

        // Register ourselves for server and other game events we are interested in
        LOGGER.info("Registering mod class to forge events.");
        forgeEventBus.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent t) {
        Devices.init();
    }

    private void loadComplete(FMLLoadCompleteEvent t) {
        Devices.loadComplete();
    }

    private void serverSetup(FMLDedicatedServerSetupEvent t) {
        Devices.serverSetup();
    }
}
