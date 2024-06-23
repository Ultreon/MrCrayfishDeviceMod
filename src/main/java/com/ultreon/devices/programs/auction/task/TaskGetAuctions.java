package com.ultreon.devices.programs.auction.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.programs.auction.AuctionManager;
import com.ultreon.devices.programs.auction.object.AuctionItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class TaskGetAuctions extends Task {
    private UUID seller;

    public TaskGetAuctions() {
        super("minebay_get_auctions");
    }

    public TaskGetAuctions(UUID seller) {
        this();
        this.seller = seller;
    }

    @Override
    public void prepareRequest(CompoundNBT nbt) {
        if (seller != null) {
            nbt.putString("seller", seller.toString());
        }
    }

    @Override
    public void processRequest(CompoundNBT nbt, World world, PlayerEntity player) {
        if (nbt.contains("seller")) {
            seller = UUID.fromString(nbt.getString("seller"));
        }
    }

    @Override
    public void prepareResponse(CompoundNBT nbt) {
        if (seller != null) {
            List<AuctionItem> items = AuctionManager.INSTANCE.getItemsForSeller(seller);
            ListNBT tagList = new ListNBT();
            items.forEach(i -> {
                CompoundNBT itemTag = new CompoundNBT();
                i.writeToNBT(itemTag);
                tagList.add(itemTag);
            });
            nbt.put("auctionItems", tagList);
        } else {
            AuctionManager.INSTANCE.writeToNBT(nbt);
        }
        this.setSuccessful();
    }

    @Override
    public void processResponse(CompoundNBT nbt) {
        AuctionManager.INSTANCE.readFromNBT(nbt);
    }
}
