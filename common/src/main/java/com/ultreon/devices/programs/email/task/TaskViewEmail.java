package com.ultreon.devices.programs.email.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.programs.email.EmailManager;
import com.ultreon.devices.programs.email.object.Email;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

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
    public void prepareRequest(CompoundNBT nbt) {
        nbt.putInt("Index", this.index);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player) {
        List<Email> emails = EmailManager.INSTANCE.getEmailsForAccount(player);
        if (emails != null) {
            int index = nbt.getInt("Index");
            if (index >= 0 && index < emails.size()) {
                emails.get(index).setRead(true);
            }
        }
    }

    @Override
    public void prepareResponse(CompoundNBT nbt) {
    }

    @Override
    public void processResponse(CompoundNBT nbt) {
    }

}
