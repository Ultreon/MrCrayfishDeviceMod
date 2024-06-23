package com.ultreon.devices.programs.system.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.programs.system.object.Account;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.UUID;

public class TaskPay extends Task {
    private String uuid;
    private int amount;

    public TaskPay() {
        super("bank_pay");
    }

    public TaskPay(String uuid, int amount) {
        this();
        this.uuid = uuid;
        this.amount = amount;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putString("player", this.uuid);
        tag.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        String uuid = tag.getString("uuid");
        int amount = tag.getInt("amount");
        Account sender = BankUtil.INSTANCE.getAccount(player);
        Account recipient = BankUtil.INSTANCE.getAccount(UUID.fromString(uuid));
        if (recipient != null && sender.hasAmount(amount)) {
            recipient.add(amount);
            sender.remove(amount);
            this.amount = sender.getBalance();
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
