package com.ultreon.devices.programs.system.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.programs.system.object.Account;
import com.ultreon.devices.util.InventoryUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;

/**
 * @author MrCrayfish
 */
public class TaskDeposit extends Task {
    private int amount;

    public TaskDeposit() {
        super("bank_deposit");
    }

    public TaskDeposit(int amount) {
        this();
        this.amount = amount;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        Account account = BankUtil.INSTANCE.getAccount(player);
        int amount = tag.getInt("amount");
        long value = account.getBalance() + amount;
        if (value < 0) {
            amount = Integer.MAX_VALUE - account.getBalance();
        }
        if (InventoryUtil.removeItemWithAmount(player, Items.EMERALD, amount)) {
            if (account.deposit(amount)) {
                this.amount = account.getBalance();
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(CompoundNBT tag) {
        tag.putInt("balance", this.amount);
    }

    @Override
    public void processResponse(CompoundNBT tag) {
    }
}
