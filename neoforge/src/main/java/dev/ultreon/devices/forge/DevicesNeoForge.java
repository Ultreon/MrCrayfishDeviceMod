package dev.ultreon.devices.forge;

import com.mojang.logging.LogUtils;
import dev.ultreon.devices.DeviceConfig;
import dev.ultreon.devices.UltreonDevicesMod;
import dev.ultreon.devices.LaunchException;
import dev.ultreon.devices.Reference;
import dev.ultreon.devices.impl.app.Application;
import dev.ultreon.devices.impl.print.IPrint;
import dev.ultreon.devices.impl.print.PrintingManager;
import dev.ultreon.devices.os.mineos.userspace.MineOS;
import dev.ultreon.devices.event.WorldDataHandler;
import dev.ultreon.devices.init.RegistrationHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Reference.MOD_ID)
public final class DevicesNeoForge {
    public static final Logger LOGGER = LogUtils.getLogger();
    private final UltreonDevicesMod instance = new UltreonDevicesMod() {
        @Override
        protected void registerApplicationEvent() {
            DevicesNeoForge.this.modEventBus.post(new NeoForgeApplicationRegistration());
        }

        @Override
        public int getBurnTime(ItemStack stack, RecipeType<?> type) {
            return stack.getBurnTime(type);
        }

        @Override
        protected List<Application> getApplications() {
            return ObfuscationReflectionHelper.getPrivateValue(MineOS.class, null, "APPLICATIONS");
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        @SuppressWarnings("DataFlowIssue")
        protected void setRegisteredRenders(Map<String, IPrint.Renderer> map) {
            ObfuscationReflectionHelper.setPrivateValue(PrintingManager.class, null, map, "registeredRenders");
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        protected Map<String, IPrint.Renderer> getRegisteredRenders() {
            return ObfuscationReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
        }
    };

    public IEventBus modEventBus;

    public DevicesNeoForge(IEventBus modEventBus) throws LaunchException {
        super();

        this.modEventBus = modEventBus;
        this.modEventBus.register(BuiltinAppsRegistration.class);

        UltreonDevicesMod.preInit();

        ModLoadingContext context = ModLoadingContext.get();
        IEventBus forgeEventBus = NeoForge.EVENT_BUS;

        // Common side stuff
        LOGGER.info("Initializing registration handler and mod config.");
        RegistrationHandler.register();
        context.registerConfig(ModConfig.Type.CLIENT, DeviceConfig.CONFIG);

        forgeEventBus.register(this);
        forgeEventBus.register(new WorldDataHandler());

        LOGGER.info("Registering common setup handler, and load complete handler.");
        this.modEventBus.addListener(this::fmlCommonSetup);
        this.modEventBus.addListener(this::fmlLoadComplete);

        // Server side stuff
        LOGGER.info("Registering server setup handler.");
        this.modEventBus.addListener(this::fmlServerSetup);

        // Client side stuff
        if (!DatagenModLoader.isRunningDataGen()) {
            LOGGER.info("Registering the reload listener.");
//            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(this);
        }

        // Register ourselves for server and other game events we are interested in
        LOGGER.info("Registering mod class to forge events.");
        forgeEventBus.register(this);
    }

    private void fmlCommonSetup(FMLCommonSetupEvent t) {
        this.instance.init();
    }

    private void fmlLoadComplete(FMLLoadCompleteEvent t) {
        this.instance.loadComplete();
    }

    private void fmlServerSetup(FMLDedicatedServerSetupEvent t) {
        this.instance.serverSetup();
    }
}
