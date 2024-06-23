package com.ultreon.devices.programs.auction.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.api.utils.BankUtil;
import com.ultreon.devices.programs.auction.AuctionManager;
import com.ultreon.devices.programs.auction.object.AuctionItem;
import com.ultreon.devices.programs.system.object.Account;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

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
    public void prepareRequest(CompoundNBT nbt) {
        nbt.putString("id", id.toString());
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player) {
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
    public void prepareResponse(CompoundNBT nbt) {
    }

    @Override
    public void processResponse(CompoundNBT nbt) {
    }
}
