package dev.ultreon.devices.mineos.apps.auction.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.api.utils.BankUtil;
import dev.ultreon.devices.mineos.apps.auction.AuctionManager;
import dev.ultreon.devices.mineos.apps.auction.object.AuctionItem;
import dev.ultreon.devices.mineos.apps.system.object.Account;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class TaskBuyItem extends Task {
    private UUID id;

    public TaskBuyItem() {
        super("minebay_buy_item");
    }

    public TaskBuyItem(UUID id) {
        this();
        this.id = id;
    }

    @Override
    public void prepareRequest(CompoundTag nbt) {
        nbt.putString("id", id.toString());
    }

    @Override
    public void processRequest(CompoundTag nbt, Level world, Player player) {
        this.id = UUID.fromString(nbt.getString("id"));
        AuctionItem item = AuctionManager.INSTANCE.getItem(id);
        if (item != null && item.isValid()) {
            int price = item.getPrice();
            Account buyer = BankUtil.INSTANCE.getAccount(player);
            Account seller = BankUtil.INSTANCE.getAccount(item.getSellerId());
            if (buyer.pay(seller, price)) {
                item.setSold();
                world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), item.getStack().copy()));
                this.setSuccessful();
            }
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt) {
    }

    @Override
    public void processResponse(CompoundTag nbt) {
    }
}
