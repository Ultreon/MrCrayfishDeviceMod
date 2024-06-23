package com.ultreon.devices.programs.system.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.programs.system.object.Account;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class TaskAdd extends Task {
    private int amount;

    public TaskAdd() {
        super("bank_add");
    }

    public TaskAdd(int amount) {
        this();
        this.amount = amount;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        int amount = tag.getInt("amount");
        Account sender = BankUtil.INSTANCE.getAccount(player);
        sender.add(amount);
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {
        tag.putInt("balance", this.amount);
    }

    @Override
    public void processResponse(CompoundNBT tag) {
    }
}
