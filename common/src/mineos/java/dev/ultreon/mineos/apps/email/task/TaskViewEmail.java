package dev.ultreon.mineos.apps.email.task;

import dev.ultreon.devices.impl.task.Task;
import dev.ultreon.mineos.apps.email.EmailManager;
import dev.ultreon.mineos.apps.email.object.Email;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class TaskViewEmail extends Task {
    private int index;

    public TaskViewEmail() {
        super("view_email");
    }

    public TaskViewEmail(int index) {
        this();
        this.index = index;
    }

    @Override
    public void prepareRequest(CompoundTag nbt) {
        nbt.putInt("Index", this.index);
    }

    @Override
    public void processRequest(CompoundTag nbt, Level world, Player player) {
        List<Email> emails = EmailManager.INSTANCE.getEmailsForAccount(player);
        if (emails != null) {
            int index = nbt.getInt("Index");
            if (index >= 0 && index < emails.size()) {
                emails.get(index).setRead(true);
            }
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt) {
    }

    @Override
    public void processResponse(CompoundTag nbt) {
    }

}
