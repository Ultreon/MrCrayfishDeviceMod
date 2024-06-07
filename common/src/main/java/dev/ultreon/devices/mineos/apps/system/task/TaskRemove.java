package dev.ultreon.devices.mineos.apps.system.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.utils.BankUtil;
import dev.ultreon.devices.mineos.apps.system.object.Account;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
    public void prepareRequest(CompoundTag tag) {
        tag.putInt("amount", this.amount);
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        this.amount = tag.getInt("amount");
        Account sender = BankUtil.INSTANCE.getAccount(player);
        if (sender.hasAmount(amount)) {
            sender.remove(amount);
            this.setSuccessful();
        }
    }

    @Override
    public void prepareResponse(CompoundTag tag) {
        if (isSuccessful()) {
            tag.putInt("balance", this.amount);
        }
    }

    @Override
    public void processResponse(CompoundTag tag) {
    }
}
