package com.ultreon.devices.programs.email.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.programs.email.EmailManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class TaskCheckEmailAccount extends Task {
    private boolean hasAccount = false;
    private String name = null;

    public TaskCheckEmailAccount() {
        super("check_email_account");
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {

    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        this.hasAccount = EmailManager.INSTANCE.hasAccount(player.getUUID());
        if (this.hasAccount) {
            this.name = EmailManager.INSTANCE.getName(player);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {
        if (this.isSucessful()) tag.putString("Name", this.name);
    }

    @Override
    public void processResponse(CompoundNBT tag) {

    }

}
