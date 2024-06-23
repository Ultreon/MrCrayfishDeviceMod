package com.ultreon.devices.api;

import com.ultreon.devices.Devices;
import com.ultreon.devices.api.app.Application;
import com.ultreon.devices.object.AppInfo;
import net.minecraft.util.ResourceLocation;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class ApplicationManager {
    private static final Map<ResourceLocation, AppInfo> APP_INFO = new HashMap<>();
    private static final Marker MARKER = MarkerFactory.getMarker("ApplicationManager");

    private ApplicationManager() {
    }

    /**
     * Registers an application into the application list
     * <p>
     * The identifier parameter is simply just an id for the application.
     * <p>
     * Example: {@code new ResourceLocation("modid:appid");}
     *
     * @param identifier the
     * @param clazz      the class of the application
     */
    @Nullable
    public static Application registerApplication(ResourceLocation identifier, Supplier<Application> app) {
        Devices.LOGGER.debug(MARKER, "Registering application {}", identifier);
        Application application = Devices.registerApplication(identifier, app);
        if (application != null) {
            APP_INFO.put(identifier, application.getInfo());
            return application;
        }
        return null;
    }

    /**
     * Get all applications that are registered. The returned collection does not include system
     * applications, see {@link #getSystemApplications()} or {@link #getAllApplications()}. Please
     * note that this list is read only and cannot be modified.
     *
     * @return the application list
     */
    public static List<AppInfo> getAvailableApplications() {
        final Predicate<AppInfo> FILTER = info -> !info.isSystemApp() && (!Devices.hasAllowedApplications() || Devices.getAllowedApplications().contains(info));
        return APP_INFO.values().stream().filter(FILTER).collect(Collectors.toList());
    }

    public static List<AppInfo> getSystemApplications() {
        return APP_INFO.values().stream().filter(AppInfo::isSystemApp).collect(Collectors.toList());
    }

    public static List<AppInfo> getAllApplications() {
        return new ArrayList<>(APP_INFO.values());
    }

    @Nullable
    public static AppInfo getApplication(String appId) {
        return APP_INFO.get(new ResourceLocation(appId.replace(".", ":")));
    }
}
