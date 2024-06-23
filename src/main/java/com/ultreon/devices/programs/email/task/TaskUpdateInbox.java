package com.ultreon.devices.programs.email.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.programs.email.EmailManager;
import com.ultreon.devices.programs.email.object.Email;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;

import java.util.List;

public class TaskUpdateInbox extends Task {
    private List<Email> emails;

    public TaskUpdateInbox() {
        super("update_inbox");
    }

    @Override
    public void prepareRequest(CompoundNBT nbt) {
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player) {
        this.emails = EmailManager.INSTANCE.getEmailsForAccount(player);
    }

    @Override
    public void prepareResponse(CompoundNBT nbt) {
        ListNBT tagList = new ListNBT();
        if (emails != null) {
            for (Email email : emails) {
                CompoundNBT emailTag = new CompoundNBT();
                email.save(emailTag);
                tagList.add(emailTag);
            }
        }
        nbt.put("emails", tagList);
    }

    @Override
    public void processResponse(CompoundNBT nbt) {
        EmailManager.INSTANCE.getInbox().clear();
        ListNBT emails = (ListNBT) nbt.get("emails");
        for (int i = 0; i < emails.size(); i++) {
            CompoundNBT emailTag = emails.getCompound(i);
            Email email = Email.readFromNBT(emailTag);
            EmailManager.INSTANCE.getInbox().add(email);
        }
    }
}
