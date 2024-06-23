package com.ultreon.devices.programs.email.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.programs.email.EmailManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class TaskRegisterEmailAccount extends Task {
    private String name;

    public TaskRegisterEmailAccount() {
        super("register_email_account");
    }

    public TaskRegisterEmailAccount(String name) {
        this();
        this.name = name;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt) {
        nbt.putString("AccountName", this.name);
    }

    @Override
    public void processRequest(CompoundNBT nbt, World level, PlayerEntity player) {
        if (EmailManager.INSTANCE.addAccount(player, nbt.getString("AccountName"))) {
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundNBT nbt) {
    }

    @Override
    public void processResponse(CompoundNBT nbt) {
    }

}
