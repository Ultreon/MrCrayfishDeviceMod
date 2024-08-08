package dev.ultreon.mineos.apps.system.task;

import dev.ultreon.devices.impl.task.Task;
import dev.ultreon.devices.impl.utils.BankUtil;
import dev.ultreon.mineos.apps.system.object.Account;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
    public void prepareRequest(CompoundTag tag) {
        tag.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        int amount = tag.getInt("amount");
        Account sender = BankUtil.INSTANCE.getAccount(player);
        sender.add(amount);
        this.setSuccessful();
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        tag.putInt("balance", this.amount);
    }

    @Override
    public void processResponse(CompoundTag tag) {
    }
}
