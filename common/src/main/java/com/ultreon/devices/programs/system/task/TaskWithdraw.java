package com.ultreon.devices.programs.system.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.programs.system.object.Account;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

/**
 * @author MrCrayfish
 */
public class TaskWithdraw extends Task {
    private int amount;

    public TaskWithdraw() {
        super("bank_withdraw");
    }

    public TaskWithdraw(int amount) {
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
        Account account = BankUtil.INSTANCE.getAccount(player);
        if (account.withdraw(amount)) {
            int stacks = amount / 64;
            for (int i = 0; i < stacks; i++) {
                level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.EMERALD, 64)));
            }

            int remaining = amount % 64;
            if (remaining > 0) {
                level.addFreshEntity(new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(Items.EMERALD, remaining)));
            }

            this.amount = account.getBalance();
            this.setSuccessful();
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
