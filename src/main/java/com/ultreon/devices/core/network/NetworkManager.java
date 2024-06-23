package com.ultreon.devices.core.network;

import com.ultreon.devices.block.entity.RouterBlockEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class NetworkManager {
    private UUID routerId;
    private BlockPos routerPos;

    private NetworkManager() {

    }

    public NetworkManager(Router router) {
        this.routerId = router.getId();
        this.routerPos = router.getPos();
    }

    public UUID getRouterId() {
        return routerId;
    }

    @Nullable
    public BlockPos getRouterPos() {
        return routerPos;
    }

    public void setRouterPos(@Nullable BlockPos routerPos) {
        this.routerPos = routerPos;
    }

    @Nullable
    public Router getRouter(@NotNull World level) {
        if (routerPos == null)
            return null;

        TileEntity blockEntity = level.getBlockEntity(routerPos);
        if (blockEntity instanceof RouterBlockEntity) {
            RouterBlockEntity router = (RouterBlockEntity) blockEntity;
            if (router.getRouter().getId().equals(routerId)) {
                return router.getRouter();
            }
        }
        return null;
    }

    public boolean isConnected() {
        return routerPos != null;
    }

    public CompoundNBT toTag() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("id", routerId.toString());
        return tag;
    }

    public static NetworkManager fromTag(CompoundNBT tag) {
        NetworkManager connection = new NetworkManager();
        connection.routerId = UUID.fromString(tag.getString("id"));
        return connection;
    }
}
