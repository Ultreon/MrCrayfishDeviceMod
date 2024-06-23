package com.ultreon.devices.programs.system.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.programs.system.object.Account;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class TaskGetBalance extends Task {
    private int balance;

    public TaskGetBalance() {
        super("bank_get_balance");
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        Account account = BankUtil.INSTANCE.getAccount(player);
        this.balance = account.getBalance();
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {
        tag.putInt("balance", this.balance);
    }

    @Override
    public void processResponse(CompoundNBT tag) {
    }
}
