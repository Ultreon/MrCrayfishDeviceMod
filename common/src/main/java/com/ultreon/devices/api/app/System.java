package com.ultreon.devices.api.app;

import com.ultreon.devices.api.io.Drive;
import com.ultreon.devices.core.Settings;
import com.ultreon.devices.core.Window;
import com.ultreon.devices.object.AppInfo;
import com.ultreon.devices.programs.system.component.FileInfo;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

/// @author MrCrayfish
public interface System {
    /// Open a context on the screen
    void openContext(Layout layout, int x, int y);

    /// Checks if the system has a context open
    ///
    /// @return has a context open
    boolean hasContext();

    /// Closes the current context on screen
    void closeContext();

    /// Gets the system settings
    ///
    /// @return the system settings
    Settings getSettings();

    /// Opens the specified application
    ///
    /// @param info the app info instance of the application to be opened
    Application launchApp(AppInfo info);

    /// Opens the specified application with an intent tag
    ///
    /// @param info      the app info instance of the application to be opened
    /// @param intentTag the tag to pass data to the initialization of an application
    Application launchApp(AppInfo info, CompoundTag intentTag);

    /// Opens the specified application with a file
    ///
    /// @param info     the app info instance of the application to be opened
    /// @param path     the file for the application to load
    /// @param callback the callback to execute when the application is opened
    default void launchApp(AppInfo info, FileInfo path, Consumer<LauncherResponse> callback) {
        @Nullable Drive drive = path.getDrive();
        if (drive == null) {
            callback.accept(new LauncherResponse("Drive not found", null, false));
            return;
        }

        Application application = loadApp(info);
        if (application == null) {
            callback.accept(new LauncherResponse("Application not found", null, false));
            return;
        }

        if (application.isOpen()) {
            callback.accept(new LauncherResponse("Application already open", null, false));
            return;
        }

        application.handleFile(path, (unit, success) -> {
            if (!success) {
                callback.accept(new LauncherResponse("Failed to open file", null, false));
                Window<?> window = application.getWindow();
                return;
            }
            callback.accept(new LauncherResponse(null, application, true));
        });
    }

    Application loadApp(AppInfo info);

    /// Closes the specified application
    ///
    /// @param info the app info instance of application to close
    void closeApplication(AppInfo info);

    /// Gets all the installed applications
    ///
    /// @return a collection of installed applications
    Collection<AppInfo> getInstalledApplications();

    void openDialog(Dialog message);
}
