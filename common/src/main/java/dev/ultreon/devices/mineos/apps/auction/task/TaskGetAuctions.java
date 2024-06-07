package dev.ultreon.devices.mineos.apps.auction.task;

import dev.ultreon.devices.api.task.Task;
import dev.ultreon.devices.mineos.apps.auction.AuctionManager;
import dev.ultreon.devices.mineos.apps.auction.object.AuctionItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
    public void prepareRequest(CompoundTag nbt) {
        if (seller != null) {
            nbt.putString("seller", seller.toString());
        }
    }

    @Override
    public void processRequest(CompoundTag nbt, Level world, Player player) {
        if (nbt.contains("seller")) {
            seller = UUID.fromString(nbt.getString("seller"));
        }
    }

    @Override
    public void prepareResponse(CompoundTag nbt) {
        if (seller != null) {
            List<AuctionItem> items = AuctionManager.INSTANCE.getItemsForSeller(seller);
            ListTag tagList = new ListTag();
            items.forEach(i -> {
                CompoundTag itemTag = new CompoundTag();
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
    public void processResponse(CompoundTag nbt) {
        AuctionManager.INSTANCE.readFromNBT(nbt);
    }
}
