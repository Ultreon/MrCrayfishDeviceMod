package com.ultreon.devices;

import com.google.common.base.Suppliers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.api.print.PrintingManager;
import com.ultreon.devices.api.task.TaskManager;
import com.ultreon.devices.api.utils.OnlineRequest;
import com.ultreon.devices.block.PrinterBlock;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.core.client.ClientNotification;
import com.ultreon.devices.core.client.debug.ClientAppDebug;
import com.ultreon.devices.core.io.task.*;
import com.ultreon.devices.core.network.task.TaskConnect;
import com.ultreon.devices.core.network.task.TaskGetDevices;
import com.ultreon.devices.core.network.task.TaskPing;
import com.ultreon.devices.core.print.task.TaskPrint;
import com.ultreon.devices.core.task.TaskInstallApp;
import com.ultreon.devices.init.DeviceItems;
import com.ultreon.devices.network.PacketHandler;
import com.ultreon.devices.network.task.SyncApplicationPacket;
import com.ultreon.devices.network.task.SyncConfigPacket;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.*;
import com.ultreon.devices.programs.auction.MineBayApp;
import com.ultreon.devices.programs.auction.task.TaskAddAuction;
import com.ultreon.devices.programs.auction.task.TaskBuyItem;
import com.ultreon.devices.programs.auction.task.TaskGetAuctions;
import com.ultreon.devices.programs.debug.TextAreaApp;
import com.ultreon.devices.programs.email.EmailApp;
import com.ultreon.devices.programs.email.task.*;
import com.ultreon.devices.programs.example.ExampleApp;
import com.ultreon.devices.programs.example.task.TaskNotificationTest;
import com.ultreon.devices.programs.gitweb.GitWebApp;
import com.ultreon.devices.programs.snake.SnakeApp;
import com.ultreon.devices.programs.system.*;
import com.ultreon.devices.programs.system.task.*;
import com.ultreon.devices.util.SiteRegistration;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.Registries;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Devices {
    public static final String MOD_ID = "devices";
    public static final ItemGroup GROUP_DEVICE = new ItemGroup("devices_tab_device") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(DeviceItems.RED_LAPTOP.get());
        }
    };
    public static final List<SiteRegistration> SITE_REGISTRATIONS = new ProtectedArrayList<>();
    public static final Logger LOGGER = LoggerFactory.getLogger("Devices Mod");
    public static final boolean DEVELOPER_MODE = false;
    private static final Pattern DEV_PREVIEW_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+-dev\\d+");
    private static final boolean IS_DEV_PREVIEW = DEV_PREVIEW_PATTERN.matcher(Reference.VERSION).matches();
    private static final String GITWEB_REGISTER_URL = "https://ultreon.gitlab.io/gitweb/site_register.json";
    private static final SiteRegisterStack SITE_REGISTER_STACK = new SiteRegisterStack();
    static List<AppInfo> allowedApps;
    private static MinecraftServer server;

    public static void init() {
        LOGGER.info("Doing some common setup.");

        PacketHandler.init();

        registerApplications();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientAppDebug.register();
            ClientModEvents.clientSetup(); //todo
        });

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> Devices::setupSiteRegistrations);

        setupEvents();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> Devices::setupClientEvents); //todo
        loadComplete();

//        ultranLang:
//        {
//            var inputFile = new File("main.ulan");
//
//            if (!inputFile.exists()) {
//                LOGGER.error("File not found: {}", inputFile.getAbsolutePath());
//            } else {
//                SpiKt.setShouldLogInternalErrors(false);
//                SpiKt.setShouldLogScope(false);
//                SpiKt.setShouldLogStack(false);
//                SpiKt.setShouldLogTokens(false);
//
//                String text;
//                try {
//                    text = Files.readString(inputFile.toPath(), Charset.defaultCharset());
//                } catch (IOException e) {
//                    LOGGER.error("Failed to read file: {}", inputFile.getAbsolutePath(), e);
//                    break ultranLang;
//                }
//
//                registerNativeFunctions();
//                NativeCalls.INSTANCE.load();
//
//                var lexer = new Lexer(text);
//                Program tree;
//                try {
//                    var parser = new Parser(lexer);
//                    tree = parser.parse();
//                } catch (LexerException | ParserException e) {
//                    if (SpiKt.getShouldLogInternalErrors()) e.printStackTrace();
//                    LOGGER.error("Error parsing file: {}", e.getMessage());
//                    break ultranLang;
//                } catch (RuntimeException e) {
//                    var cause = e.getCause();
//                    while (cause instanceof InvocationTargetException || cause instanceof RuntimeException) {
//                        cause = cause.getCause();
//                    }
//                    if (cause instanceof LexerException) {
//                        if (SpiKt.getShouldLogInternalErrors()) cause.printStackTrace();
//                        LOGGER.error("Error parsing file: {}", cause.getMessage());
//                    } else if (cause instanceof ParserException) {
//                        if (SpiKt.getShouldLogInternalErrors()) cause.printStackTrace();
//                        LOGGER.error("Error parsing file: {}", cause.getMessage());
//                    } else {
//                        throw e;
//                    }
//                    break ultranLang;
//                }
//
//                var semanticAnalyzer = new SemanticAnalyzer();
//
//                try {
//                    semanticAnalyzer.visit(tree);
//                } catch (SemanticException e) {
//                    if (SpiKt.getShouldLogInternalErrors()) e.printStackTrace();
//                    LOGGER.error("Error analyzing file: {}", e.getMessage());
//                    break ultranLang;
//                } catch (RuntimeException e) {
//                    var cause = e.getCause();
//                    while (cause instanceof InvocationTargetException || cause instanceof RuntimeException) {
//                        cause = cause.getCause();
//                    }
//                    if (cause instanceof SemanticException) {
//                        if (SpiKt.getShouldLogInternalErrors()) cause.printStackTrace();
//                        LOGGER.error("Error analyzing file: {}", cause.getMessage());
//                    } else {
//                        throw e;
//                    }
//                    break ultranLang;
//                }
//
//                try {
//                    var interpreter = new Interpreter(tree);
//                    interpreter.interpret();
//                } catch (Exception e) {
//                    if (SpiKt.getShouldLogInternalErrors()) e.printStackTrace();
//                    LOGGER.error("Error interpreting file: {}", e.getMessage());
//                }
//            }
//        }
    }

//    private static void registerNativeFunctions() {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("level", BuiltinTypeSymbol.STRING);
//        params.put("message", BuiltinTypeSymbol.STRING);
//        NativeCalls.INSTANCE.register("log", params, ar -> {
//            Object level = ar.get("level");
//            if (level instanceof String levelName) {
//                Object message = ar.get("message");
//                if (message == null) message = "null";
//
//                switch (levelName.toLowerCase(Locale.ROOT)) {
//                    case "warn" -> LOGGER.warn(message.toString());
//                    case "error" -> LOGGER.error(message.toString());
//                    case "debug" -> LOGGER.debug(message.toString());
//                    case "trace" -> LOGGER.trace(message.toString());
//                    default -> LOGGER.info(message.toString());
//                }
//            } else {
//                throw new IllegalArgumentException("Invalid level of type " + (level == null ? "null" : level.getClass().getName()));
//            }
//            return null;
//        });
//    }

    public static void preInit() {
        if (DEVELOPER_MODE && FMLEnvironment.production) {
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


    public static void serverSetup() {
        LOGGER.info("Doing some server setup.");
    }

    public static void loadComplete() {
        LOGGER.info("Doing some load complete handling.");
    }


    private static void registerApplications() {
        // Applications (Both)
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "diagnostics"), DiagnosticsApp::new);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "settings"), SettingsApp::new);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "bank"), BankApp::new);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "file_browser"), FileBrowserApp::new);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "gitweb"), GitWebApp::new);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "note_stash"), NoteStashApp::new);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "pixel_painter"), PixelPainterApp::new);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "ender_mail"), EmailApp::new);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "app_store"), AppStore::new);

        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "boat_racers"), BoatRacersApp::new);
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "mine_bay"), MineBayApp::new);

        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "snake"), SnakeApp::new);

        // Core
        TaskManager.registerTask(TaskUpdateApplicationData::new);
        TaskManager.registerTask(TaskPrint::new);
        TaskManager.registerTask(TaskUpdateSystemData::new);
        TaskManager.registerTask(TaskConnect::new);
        TaskManager.registerTask(TaskPing::new);
        TaskManager.registerTask(TaskGetDevices::new);

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

        // Auction
        TaskManager.registerTask(TaskAddAuction::new);
        TaskManager.registerTask(TaskGetAuctions::new);
        TaskManager.registerTask(TaskBuyItem::new);

        if (DEVELOPER_MODE) {
            // Applications (Developers)
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "example"), ExampleApp::new);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "icons"), IconsApp::new);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "text_area"), TextAreaApp::new);
            ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "test"), TestApp::new);

            TaskManager.registerTask(TaskNotificationTest::new);
        }

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PrintingManager.registerPrint(new ResourceLocation(Reference.MOD_ID, "picture"), PixelPainterApp.PicturePrint.class));
    }
    public static List<Application> getAPPLICATIONS() {
        return ObfuscationReflectionHelper.getPrivateValue(Laptop.class, null, "APPLICATIONS");
    }

    public static Map<String, IPrint.Renderer> getRegisteredRenders(){
        return ObfuscationReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
    }

    public static void setRegisteredRenders(Map<String, IPrint.Renderer> map){
        ObfuscationReflectionHelper.setPrivateValue(PrintingManager.class, null, map, "registeredRenders");
    }
    public static void setAllowedApps(List<AppInfo> allowedApps) {
        Devices.allowedApps = allowedApps;
    }

    @Nullable
    public static Application registerApplication(ResourceLocation identifier, Supplier<Application> app) {
        if ("minecraft".equals(identifier.getNamespace())) {
            throw new IllegalArgumentException("Identifier cannot be \"minecraft\"!");
        }

        if (allowedApps == null) {
            allowedApps = new ArrayList<>();
        }

        Application appl = app.get();
        if (app instanceof SystemApp) {
            allowedApps.add(new AppInfo(identifier, true));
        } else {
            allowedApps.add(new AppInfo(identifier, false));
        }

        AtomicReference<Application> application = new AtomicReference<>(null);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            List<Application> apps = getAPPLICATIONS(); /*ObfuscationReflectionHelper.getPrivateValue(Laptop.class, null, "APPLICATIONS");*/
            assert apps != null;
            apps.add(appl);

            appl.setInfo(generateAppInfo(identifier, appl.getClass()));

            application.set(appl);
        });
        return application.get();
    }

    @NotNull
    private static AppInfo generateAppInfo(ResourceLocation identifier, Class<? extends Application> clazz) {
        LOGGER.debug("Generating app info for " + identifier.toString());

        AppInfo info = new AppInfo(identifier, SystemApp.class.isAssignableFrom(clazz));
        info.reload();
        return info;
    }

    public static boolean registerPrint(ResourceLocation identifier, Class<? extends IPrint> classPrint) {
        LOGGER.debug("Registering print: " + identifier.toString());

        try {
            Constructor<? extends IPrint> constructor = classPrint.getConstructor();
            IPrint print = constructor.newInstance();
            Class<? extends IPrint.Renderer> classRenderer = print.getRenderer();
            try {
                IPrint.Renderer renderer = classRenderer.getConstructor().newInstance();
                Map<String, IPrint.Renderer> idToRenderer = getRegisteredRenders(); //ObfuscationReflectionHelper.getPrivateValue(PrintingManager.class, null, "registeredRenders");
                if (idToRenderer == null) {
                    idToRenderer = new HashMap<>();
                    setRegisteredRenders(idToRenderer);
                    //ObfuscationReflectionHelper.setPrivateValue(PrintingManager.class, null, idToRenderer, "registeredRenders");
                }
                idToRenderer.put(identifier.toString(), renderer);
            } catch (InstantiationException e) {
                Devices.LOGGER.error("The print renderer '" + classRenderer.getName() + "' is missing an empty constructor and could not be registered!");
                return false;
            }
            return true;
        } catch (Exception e) {
            Devices.LOGGER.error("The print '" + classPrint.getName() + "' is missing an empty constructor and could not be registered!");
        }
        return false;
    }

    public static void showNotification(CompoundNBT tag) {
        LOGGER.debug("Showing notification");

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
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
        return new ResourceLocation(Devices.MOD_ID, path);
    }

//    private void enqueueIMC(final InterModEnqueueEvent event) {
//        // Check for self availability.
//        InterModComms.sendTo(Reference.MOD_ID, "availability", () -> {
//            LOGGER.info("IMC is working correctly");
//            return "Hello world";
//        });
//    }
//
//    private void processIMC(final InterModProcessEvent event) {
//        event.getIMCStream().forEachOrdered(imcMessage -> {
//            // Availability IMC handling.
//            if (imcMessage.method().equals("availability")) {
//                LOGGER.info("Received message from " + imcMessage.senderModId() + ": " + imcMessage.messageSupplier().get());
//            }
//        });
//    }

    private static void setupClientEvents() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register((player -> {
            LOGGER.debug("Client disconnected from server");

            allowedApps = null;
            DeviceConfig.restore();
        }));
    }

    private static void setupEvents() {
        LifecycleEvent.SERVER_STARTING.register((instance -> server = instance));
        LifecycleEvent.SERVER_STOPPED.register(instance -> server = null);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(((player, hand, pos, face) -> {
            World level = player.getLevel();
            if (!player.getItemInHand(hand).isEmpty() && player.getItemInHand(hand).getItem() == Items.PAPER) {
                if (level.getBlockState(pos).getBlock() instanceof PrinterBlock) {
                    return EventResult.interruptTrue();
                    //event.setUseBlock(Event.Result.ALLOW); //todo
                }
            }
            return EventResult.pass();
        }));

        PlayerEvent.PLAYER_JOIN.register((player -> {
            LOGGER.info("Player logged in: " + player.getName());

            if (allowedApps != null) {
                PacketHandler.sendToClient(new SyncApplicationPacket(allowedApps), player);
            }
            PacketHandler.sendToClient(new SyncConfigPacket(), player);
        }));
    }

    private static void setupSiteRegistrations() {
        setupSiteRegistration(GITWEB_REGISTER_URL);
    }

    private static void setupSiteRegistration(String url) {
        SITE_REGISTER_STACK.push();

        OnlineRequest.getInstance().make(url, (success, response) -> {
            if (success) {
                //Minecraft.getInstance().doRunTask(() -> {
                JsonArray array = JsonParser.parseString(response).getAsJsonArray();
                for (JsonElement jsonElement : array) {
                    JsonObject elem = jsonElement.getAsJsonObject();
                    String registrant = elem.get("registrant") != null ? elem.get("registrant").getAsString() : null;
                    Type type = Type.REGISTRATION;
                    JsonElement typeElem;
                    if ((typeElem = elem.get("type")) != null && typeElem.isJsonPrimitive() && typeElem.getAsJsonPrimitive().isString()) {
                        switch (typeElem.getAsString()) {
                            case "registration":
                                break;
                            case "site-register":
                                type = Type.SITE_REGISTER;
                                break;
                            default:
                                LOGGER.error("Invalid element type: " + typeElem.getAsString());
                                continue;
                        }
                    }

                    switch (type) {
                        case REGISTRATION:
                            @SuppressWarnings("all") //no
                            var dev = elem.get("dev") != null ? elem.get("dev").getAsBoolean() : false;
                            String site = elem.get("site").getAsString();
                            if (dev && !IS_DEV_PREVIEW) {
                                continue;
                            }
                            for (JsonElement registration : elem.get("registrations").getAsJsonArray()) {
                                var a = registration.getAsJsonObject().keySet();
                                JsonObject d = registration.getAsJsonObject();
                                for (String string : a) {
                                    String registrationType = d.get(string).getAsString();
                                    SITE_REGISTRATIONS.add(new SiteRegistration(registrant, string, registrationType, site));
                                }
                            }
                            break;
                        case SITE_REGISTER:
                            if (!elem.has("register") || !elem.get("register").isJsonPrimitive() || !elem.get("register").getAsJsonPrimitive().isString()) {
                                continue;
                            }
                            String registerUrl = elem.get("register").getAsString();
                            try {
                                setupSiteRegistration(registerUrl);
                            } catch (Exception e) {
                                LOGGER.error("Error when loading site register: " + registerUrl);
                            }
                            break;
                    }
                }
            } else {
                LOGGER.error("Error occurred when loading site registrations at: " + url);
            }
            SITE_REGISTER_STACK.pop();
        });
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MOD_ID, id);
    }

    private enum Type {
        SITE_REGISTER, REGISTRATION
    }

    private static class ProtectedArrayList<T> extends ArrayList<T> {
        private final StackWalker stackWalker = StackWalker.getInstance(EnumSet.of(StackWalker.Option.RETAIN_CLASS_REFERENCE));
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
            if (stackWalker.getCallerClass() != Devices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.add(t);
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            freezeCheck();
            if (stackWalker.getCallerClass() != Devices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.addAll(c);
        }

        @Override
        public void add(int index, T element) {
            freezeCheck();
            if (stackWalker.getCallerClass() != Devices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            super.add(index, element);
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            freezeCheck();
            if (stackWalker.getCallerClass() != Devices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            super.removeRange(fromIndex, toIndex);
        }

        @Override
        public boolean remove(Object o) {
            freezeCheck();
            if (stackWalker.getCallerClass() != Devices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.remove(o);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            freezeCheck();
            if (stackWalker.getCallerClass() != Devices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.removeAll(c);
        }

        @Override
        public boolean removeIf(Predicate<? super T> filter) {
            freezeCheck();
            if (stackWalker.getCallerClass() != Devices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.removeIf(filter);
        }

        @Override
        public T remove(int index) {
            freezeCheck();
            if (stackWalker.getCallerClass() != Devices.class) {
                throw new IllegalCallerException("Should be called from Devices Mod main class.");
            }
            return super.remove(index);
        }
    }

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