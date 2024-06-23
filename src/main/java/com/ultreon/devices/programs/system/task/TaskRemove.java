package com.ultreon.devices.programs.system.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.programs.system.object.Account;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class TaskRemove extends Task {
    private int amount;

    public TaskRemove() {
        super("bank_remove");
    }

    public TaskRemove(int amount) {
        this();
        this.amount = amount;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        this.amount = tag.getInt("amount");
        Account sender = BankUtil.INSTANCE.getAccount(player);
        if (sender.hasAmount(amount)) {
            sender.remove(amount);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {
        if (isSucessful()) {
            tag.putInt("balance", this.amount);
        }
    }

    @Override
    public void processResponse(CompoundNBT tag) {
    }
}
