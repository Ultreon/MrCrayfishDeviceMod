package dev.ultreon.devices;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import com.mojang.serialization.Lifecycle;
import dev.ultreon.devices.api.ApplicationManager;
import dev.ultreon.devices.api.ApplicationSupplier;
import dev.ultreon.devices.api.app.Application;
import dev.ultreon.devices.api.print.IPrint;
import dev.ultreon.devices.api.print.PrintingManager;
import dev.ultreon.devices.api.task.TaskManager;
import dev.ultreon.devices.api.util.Vulnerability;
import dev.ultreon.devices.api.utils.OnlineRequest;
import dev.ultreon.devices.mineos.apps.email.task.TaskRegisterEmailAccount;
import dev.ultreon.devices.mineos.apps.email.task.*;
import dev.ultreon.devices.mineos.apps.system.task.TaskAdd;
import dev.ultreon.devices.core.BootLoader;
import dev.ultreon.devices.core.client.ClientNotification;
import dev.ultreon.devices.core.network.task.TaskConnect;
import dev.ultreon.devices.core.network.task.TaskGetDevices;
import dev.ultreon.devices.core.network.task.TaskPing;
import dev.ultreon.devices.core.print.task.TaskPrint;
import dev.ultreon.devices.core.task.TaskInstallApp;
import dev.ultreon.devices.block.PrinterBlock;
import dev.ultreon.devices.core.io.task.*;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.mineos.apps.system.task.*;
import dev.ultreon.devices.network.PacketHandler;
import dev.ultreon.devices.network.task.SyncApplicationPacket;
import dev.ultreon.devices.network.task.SyncConfigPacket;
import dev.ultreon.devices.object.AppInfo;
import dev.ultreon.devices.object.TrayItem;
import dev.ultreon.devices.mineos.apps.IconsApp;
import dev.ultreon.devices.mineos.apps.PixelPainterApp;
import dev.ultreon.devices.mineos.apps.TestApp;
import dev.ultreon.devices.mineos.apps.auction.task.TaskAddAuction;
import dev.ultreon.devices.mineos.apps.auction.task.TaskBuyItem;
import dev.ultreon.devices.mineos.apps.auction.task.TaskGetAuctions;
import dev.ultreon.devices.mineos.apps.debug.TextAreaApp;
import dev.ultreon.devices.mineos.apps.example.ExampleApp;
import dev.ultreon.devices.mineos.apps.example.task.TaskNotificationTest;
import dev.ultreon.devices.mineos.apps.system.SystemApp;
import dev.ultreon.devices.util.SiteRegistration;
import dev.ultreon.devices.util.VulnerabilityUtil;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.MappedRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public abstract class UltreonDevicesMod {
    public static final boolean DEVELOPER_MODE = Platform.isDevelopmentEnvironment();
    public static final String MOD_ID = "devices";
    public static final Logger LOGGER = LoggerFactory.getLogger("Devices Mod");

    public static final DeferredSupplier<CreativeModeTab> TAB_DEVICE = DeviceTab.create();
    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));
    public static final List<SiteRegistration> SITE_REGISTRATIONS = new ProtectedArrayList<>();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final DevicesEarlyConfig EARLY_CONFIG = DevicesEarlyConfig.load();
    public static final Registrar<BootLoader<?>> OPERATING_SYSTEM = REGISTRIES.get().<BootLoader<?>>builder(res("operating_system")).syncToClients().build();

    private static final Pattern DEV_PREVIEW_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+-dev\\d+");
    private static final boolean IS_DEV_PREVIEW = DEV_PREVIEW_PATTERN.matcher(Reference.VERSION).matches();

    public static final String GITWEB_REGISTER_URL = "https://ultreon.gitlab.io/gitweb/site_register.json";
    public static final String VULNERABILITIES_URL = "https://jab125.com/gitweb/vulnerabilities.php";

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final SiteRegisterStack SITE_REGISTER_STACK = new SiteRegisterStack();

    //---- Registry : Start ----//
    public static MappedRegistry<TrayItem> trayItemRegistry = new MappedRegistry<>(ResourceKey.createRegistryKey(id("tray_item")), Lifecycle.stable());
    //---- Registry : End ----//

    static List<AppInfo> allowedApps = new ArrayList<>();
    private static List<Vulnerability> vulnerabilities = new ArrayList<>();
    private static UltreonDevicesMod instance;

    public static List<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }
    private static MinecraftServer server;
    private static TestManager tests;

    protected UltreonDevicesMod() {
        UltreonDevicesMod.instance = this;
    }

    public static UltreonDevicesMod get() {
        return instance;
    }

    public void init() {
        if (ArchitecturyTarget.getCurrentTarget().equals("fabric")) {
            preInit();
            serverSetup();
        }

        // STOPSHIP: 3/11/2022 should be moved to dedicated testmod
        final var property = System.getProperty("ultreon.devices.tests");
        tests = new TestManager();
        if (property != null) {
            String[] split = property.split(",");
            tests.load(Set.of(split));
        }

        LOGGER.info("Doing some common setup.");

        PacketHandler.init();

        registerApplications();

        OperatingSystems.init();

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            ClientModEvents.clientSetup();
            UltreonDevicesMod.setupSiteRegistrations();
            UltreonDevicesMod.checkForVulnerabilities();
        });

        setupEvents();

        EnvExecutor.runInEnv(Env.CLIENT, () -> UltreonDevicesMod::setupClientEvents);
        if (!ArchitecturyTarget.getCurrentTarget().equals("forge")) {
            loadComplete();
        }
    }

    public static void preInit() {
        if (DEVELOPER_MODE && !Platform.isDevelopmentEnvironment()) {
            throw new LaunchException();
        }

        DeviceConfig.init();
    }


    public static boolean isDevelopmentPreview() {
        return IS_DEV_PREVIEW;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static TestManager getTests() {
        return tests;
    }

    public void serverSetup() {
        LOGGER.info("Doing some server setup.");
    }

    public void loadComplete() {
        LOGGER.info("Doing some load complete handling.");
    }


    private void registerApplications() {
        // Applications (Both)

        registerApplicationEvent();
        // Core
        TaskManager.registerTask(TaskUpdateApplicationData::new);
        TaskManager.registerTask(TaskPrint::new);
        TaskManager.registerTask(TaskUpdateSystemData::new);
        TaskManager.registerTask(TaskConnect::new);
        TaskManager.registerTask(TaskPing::new);
        TaskManager.registerTask(TaskGetDevices::new);
        TaskManager.registerTask(TaskGetRouters::new);

        // Bank
        TaskManager.registerTask(TaskDeposit::new);
        TaskManager.registerTask(TaskWithdraw::new);
        TaskManager.registerTask(TaskGetBalance::new);
        TaskManager.registerTask(TaskPay::new);
        TaskManager.registerTask(TaskAdd::new);
        TaskManager.registerTask(TaskRemove::new);

        // File browser
        TaskManager.registerTask(TaskSendAction::new);
        TaskManager.registerTask(TaskSetupFileBrowser::new);
        TaskManager.registerTask(TaskGetFiles::new);
        TaskManager.registerTask(TaskGetStructure::new);
        TaskManager.registerTask(TaskGetMainDrive::new);

        // App Store
        TaskManager.registerTask(TaskInstallApp::new);

        // Ender Mail
        TaskManager.registerTask(TaskUpdateInbox::new);
        TaskManager.registerTask(TaskSendEmail::new);
        TaskManager.registerTask(TaskCheckEmailAccount::new);
        TaskManager.registerTask(TaskRegisterEmailAccount::new);
        TaskManager.registerTask(TaskDeleteEmail::new);
        TaskManager.registerTask(TaskViewEmail::new);

        if (Platform.isDevelopmentEnvironment() || UltreonDevicesMod.EARLY_CONFIG.enableBetaApps) {
            // Auction
            TaskManager.registerTask(TaskAddAuction::new);
            TaskManager.registerTask(TaskGetAuctions::new);
            TaskManager.registerTask(TaskBuyItem::new);

            // Bank
            TaskManager.registerTask(TaskDeposit::new);
            TaskManager.registerTask(TaskWithdraw::new);
            TaskManager.registerTask(TaskGetBalance::new);
            TaskManager.registerTask(TaskPay::new);
            TaskManager.registerTask(TaskAdd::new);
            TaskManager.registerTask(TaskRemove::new);
        }

        if (Platform.isDevelopmentEnvironment() || UltreonDevicesMod.EARLY_CONFIG.enableDebugApps) {
            // Applications (Developers)
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "example"), () -> ExampleApp::new, false);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "icons"), () -> IconsApp::new, false);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "text_area"), () -> TextAreaApp::new, false);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "test"), () -> TestApp::new, false);

            TaskManager.registerTask(TaskNotificationTest::new);
        }

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> PrintingManager.registerPrint(new ResourceLocation(Reference.MOD_ID, "picture"), PixelPainterApp.PicturePrint.class));
    }

    public abstract int getBurnTime(ItemStack stack, RecipeType<?> type);

    protected abstract void registerApplicationEvent();

    @Environment(EnvType.CLIENT)
    protected abstract List<Application> getApplications();

    public static void setAllowedApps(List<AppInfo> allowedApps) {
        UltreonDevicesMod.allowedApps = allowedApps;
    }

    public static String getModVersion() {
        return Platform.getMod(MOD_ID).getVersion();
    }

    /**
     * DO NOT CALL: FOR INTERNAL USE ONLY
     */
    @Nullable
    @ApiStatus.Internal
    public Application registerApplication(ResourceLocation identifier, ApplicationSupplier app) {
        if ("minecraft".equals(identifier.getNamespace())) {
            throw new IllegalArgumentException("Identifier cannot be \"minecraft\"!");
        }

        if (allowedApps == null) {
            allowedApps = new ArrayList<>();
        }

        if (app.isSystem()) {
            allowedApps.add(new AppInfo(identifier, true));
        } else {
            allowedApps.add(new AppInfo(identifier, false));
        }

        AtomicReference<Application> application = new AtomicReference<>(null);
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            Application appl = app.get().get();
            List<Application> apps = getApplications();
            assert apps != null;
            apps.add(appl);

            appl.setInfo(generateAppInfo(identifier, appl.getClass()));

            application.set(appl);
        });
        return application.get();
    }

    @NotNull
    @Environment(EnvType.CLIENT)
    private static AppInfo generateAppInfo(ResourceLocation identifier, Class<? extends Application> clazz) {
        DebugLog.log("Generating app info for {}" + identifier.toString());

        AppInfo info = new AppInfo(identifier, SystemApp.class.isAssignableFrom(clazz));
        info.reload();
        return info;
    }

    @Environment(EnvType.CLIENT)
    protected abstract Map<String, IPrint.Renderer> getRegisteredRenders();

    @Environment(EnvType.CLIENT)
    protected abstract void setRegisteredRenders(Map<String, IPrint.Renderer> map);

    @Environment(EnvType.CLIENT)
    public boolean registerPrint(ResourceLocation identifier, Class<? extends IPrint> classPrint) {
        DebugLog.log("Registering print: %s", identifier.toString());

        try {
            Constructor<? extends IPrint> constructor = classPrint.getConstructor();
            IPrint print = constructor.newInstance();
            Class<? extends IPrint.Renderer> classRenderer = print.getRenderer();
            try {
                IPrint.Renderer renderer = classRenderer.getConstructor().newInstance();
                Map<String, IPrint.Renderer> idToRenderer = getRegisteredRenders();
                if (idToRenderer == null) {
                    idToRenderer = new HashMap<>();
                    setRegisteredRenders(idToRenderer);
                }
                idToRenderer.put(identifier.toString(), renderer);
            } catch (InstantiationException e) {
                UltreonDevicesMod.LOGGER.error("The print renderer '{}' is missing an empty constructor and could not be registered!", classRenderer.getName());
                return false;
            }
            return true;
        } catch (Exception e) {
            UltreonDevicesMod.LOGGER.error("The print '{}' is missing an empty constructor and could not be registered!", classPrint.getName());
        }
        return false;
    }

    public static void showNotification(CompoundTag tag) {
        LOGGER.debug("Showing notification");

        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            ClientNotification notification = ClientNotification.loadFromTag(tag);
            notification.push();
        });
    }

    public static boolean hasAllowedApplications() {
        return allowedApps != null;
    }

    public static List<AppInfo> getAllowedApplications() {
        if (allowedApps == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(allowedApps);
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(UltreonDevicesMod.MOD_ID, path);
    }

    private static void setupClientEvents() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register((player -> {
            DebugLog.log("Client disconnected from server");

            allowedApps = null;
            DeviceConfig.restore();
        }));
    }

    private static void setupEvents() {
        LifecycleEvent.SERVER_STARTING.register((instance -> server = instance));
        LifecycleEvent.SERVER_STOPPED.register(instance -> server = null);

        PlayerEvent.PLAYER_JOIN.register((player -> {
            LOGGER.info("Player logged in: {}", player.getName());

            if (allowedApps != null) {
                PacketHandler.sendToClient(new SyncApplicationPacket(allowedApps), player);
            }
            PacketHandler.sendToClient(new SyncConfigPacket(), player);
        }));
    }

    private static void setupSiteRegistrations() {
        setupSiteRegistration(GITWEB_REGISTER_URL);
    }

    private static void checkForVulnerabilities() {
        OnlineRequest.getInstance().make(VULNERABILITIES_URL, ((success, response) -> {
            if (!success) {
                LOGGER.error("Could not access vulnerabilities!");
                vulnerabilities = ImmutableList.of();
                return;
            }

            JsonArray array = JsonParser.parseString(response).getAsJsonArray();
            vulnerabilities = VulnerabilityUtil.parseArray(array);
            vulnerabilities.forEach(vul -> {
                String s = vul.toPrettyString();
                s.lines().toList().forEach(line -> LOGGER.debug("[VulChecker] {}", line));
                LOGGER.debug("[VulChecker]");
            });
        }));
    }

    private static CompletableFuture<Void> setupSiteRegistration(String url) {
        SITE_REGISTER_STACK.push();

        enum Type {
            SITE_REGISTER, REGISTRATION
        }

        CompletableFuture<Void> future = new CompletableFuture<>();

        OnlineRequest.getInstance().make(url, (success, response) -> CompletableFuture.runAsync(() -> {
            if (success) {
                JsonElement root = JsonParser.parseString(response);
                DebugLog.log("root = " + root);
                JsonArray rootArray = root.getAsJsonArray();
                for (JsonElement rootRegister : rootArray) {
                    DebugLog.log("rootRegister = " + rootRegister);
                    JsonObject elem = rootRegister.getAsJsonObject();
                    parseRegister(elem, future);
                }
            } else {
                LOGGER.error("Error occurred when loading site registrations at: " + url);
                return;
            }
            SITE_REGISTER_STACK.pop();
        }));

        return future;
    }

    private static void parseRegister(JsonObject elem, CompletableFuture<Void> future) {
        var registrant = elem.get("registrant") != null ? elem.get("registrant").getAsString() : null;
        RegisterType type;
        JsonElement typeElem;
        if ((typeElem = elem.get("type")) != null && typeElem.isJsonPrimitive() && typeElem.getAsJsonPrimitive().isString()) {
            String asString = typeElem.getAsString();
            if (asString.equals("registration")) {
                type = RegisterType.REGISTRATION;
            } else if (asString.equals("site-register")) {
                type = RegisterType.SITE_REGISTER;
            } else {
                LOGGER.error("Invalid element type: {}", typeElem.getAsString());
                future.complete(null);
                return;
            }
        } else {
            type = RegisterType.REGISTRATION;
        }

        if (type == RegisterType.REGISTRATION) {
            addRegistration(elem, registrant);
        } else {
            addSiteRegister(elem);
        }
        future.complete(null);
    }

    private enum RegisterType {
        SITE_REGISTER, REGISTRATION
    }

    private static void addSiteRegister(JsonObject elem) {
        if (!elem.has("register") || !elem.get("register").isJsonPrimitive() || !elem.get("register").getAsJsonPrimitive().isString()) {
            return;
        }
        var registerUrl = elem.get("register").getAsString();
        try {
            var registerFuture = setupSiteRegistration(registerUrl);
            registerFuture.join();
        } catch (Exception e) {
            LOGGER.error("Error when loading site register: {}", registerUrl);
        }
    }

    private static void addRegistration(JsonObject elem, String registrant) {
        @SuppressWarnings("all") //no
        var dev = elem.get("dev") != null ? elem.get("dev").getAsBoolean() : false;
        var site = elem.get("site").getAsString();
        if (dev && !IS_DEV_PREVIEW) {
            return;
        }
        for (JsonElement registration : elem.get("registrations").getAsJsonArray()) {
            var a = registration.getAsJsonObject().keySet();
            var d = registration.getAsJsonObject();
            for (String string : a) {
                var registrationType = d.get(string).getAsString();
                SITE_REGISTRATIONS.add(new SiteRegistration(registrant, string, registrationType, site));
            }
        }
    }

    @ApiStatus.Obsolete // Jab125 wanted this...
    public static ResourceLocation id(String path) {
        return res(path);
    }

    public boolean isDebug() {
        return Platform.isDevelopmentEnvironment();
    }

    private static class ProtectedArrayList<T> extends ArrayList<T> {
        private final transient StackWalker stackWalker = StackWalker.getInstance(EnumSet.of(StackWalker.Option.RETAIN_CLASS_REFERENCE));
        private boolean frozen = false;

        private void freeze() {
            frozen = true;
        }

        private void freezeCheck() {
            if (frozen) throw new IllegalStateException("Already frozen!");
        }

        @Override
        public boolean add(T t) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevicesMod.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.add(t);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevicesMod.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.addAll(c);
        }

        @Override
        public void add(int index, T element) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevicesMod.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            super.add(index, element);
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevicesMod.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            super.removeRange(fromIndex, toIndex);
        }

        @Override
        public boolean remove(Object o) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevicesMod.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.remove(o);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevicesMod.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.removeAll(c);
        }

        @Override
        public boolean removeIf(Predicate<? super T> filter) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevicesMod.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.removeIf(filter);
        }

        @Override
        public T remove(int index) {
            freezeCheck();
            if (stackWalker.getCallerClass() != UltreonDevicesMod.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.remove(index);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    private static class SiteRegisterStack extends Stack<Object> {
        public Object push() {
            return super.push(new Object());
        }

        @Override
        public synchronized Object pop() {
            Object pop = super.pop();
            if (isEmpty()) {
                ((ProtectedArrayList<SiteRegistration>) SITE_REGISTRATIONS).freeze();
            }
            return pop;
        }
    }


}