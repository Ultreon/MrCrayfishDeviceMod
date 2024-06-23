package com.ultreon.devices.forge;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import com.ultreon.devices.LaunchException;
import com.ultreon.devices.Reference;
import com.ultreon.devices.event.WorldDataHandler;
import com.ultreon.devices.init.RegistrationHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DatagenModLoader;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public class DevicesForge {
    private static final Pattern DEV_PREVIEW_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+-dev\\d+");
    private static final boolean IS_DEV_PREVIEW = DEV_PREVIEW_PATTERN.matcher(Reference.VERSION).matches();

    public static final Logger LOGGER = LogManager.getLogger("UltreonDevicesMod");

    public static final boolean DEVELOPER_MODE = false;

    public DevicesForge() throws LaunchException {
        Devices.preInit();

        FMLJavaModLoadingContext javaFmlLoadingCtx = FMLJavaModLoadingContext.get();
        ModLoadingContext loadingCtx = ModLoadingContext.get();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        IEventBus modEventBus = javaFmlLoadingCtx.getModEventBus();

        // Common side stuff
        forgeEventBus.register(this);
        forgeEventBus.register(new WorldDataHandler());

        LOGGER.info("Registering common setup handler, and load complete handler.");

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(this::clientSetup));
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

    @OnlyIn(Dist.CLIENT)
    private void clientSetup(FMLClientSetupEvent event) {
        RegistrationHandler.registerClient();
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
