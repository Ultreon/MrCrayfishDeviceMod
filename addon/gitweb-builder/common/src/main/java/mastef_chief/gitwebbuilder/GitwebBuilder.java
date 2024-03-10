package mastef_chief.gitwebbuilder;

import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.task.TaskManager;
import mastef_chief.gitwebbuilder.app.GWBApp;
import mastef_chief.gitwebbuilder.app.tasks.TaskNotificationCopiedCode;
import mastef_chief.gitwebbuilder.app.tasks.TaskNotificationCopiedLink;
import net.minecraft.resources.ResourceLocation;

public class GitwebBuilder {

    public static GitwebBuilder INSTANCE;

    public GitwebBuilder() {
        INSTANCE = this;
        TaskManager.registerTask(TaskNotificationCopiedCode::new);
        TaskManager.registerTask(TaskNotificationCopiedLink::new);
    }

    public static void registerApplications() {
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "gitwebbuilder_app"), () -> GWBApp::new, false);
    }
}
