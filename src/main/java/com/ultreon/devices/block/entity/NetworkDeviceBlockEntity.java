package com.ultreon.devices.block.entity;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.core.network.NetworkManager;
import com.ultreon.devices.core.network.Router;
import com.ultreon.devices.util.Colorable;
import com.ultreon.devices.util.Tickable;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class NetworkDeviceBlockEntity extends DeviceBlockEntity implements Tickable {
    private int counter;
    private NetworkManager connection;

    public NetworkDeviceBlockEntity(TileEntityType<?> pType) {
        super(pType);
    }

    @Override
    public void tick() {
        assert level != null;
        if (level.isClientSide)
            return;

        if (connection != null) {
            if (++counter >= DeviceConfig.BEACON_INTERVAL.get() * 2) {
                connection.setRouterPos(null);
                counter = 0;
            }
        }
    }

    public void connect(Router router) {
        assert level != null;
        if (router == null) {
            if (connection != null) {
                Router connectedRouter = connection.getRouter(level);
                if (connectedRouter != null) {
                    connectedRouter.removeDevice(this);
                }
            }

            connection = null;
            return;
        }
        connection = new NetworkManager(router);
        counter = 0;
        this.setChanged();
    }

    public NetworkManager getConnection() {
        return connection;
    }

    @Nullable
    public Router getRouter() {
        return connection != null ? connection.getRouter(Objects.requireNonNull(level)) : null;
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public boolean receiveBeacon(Router router) {
        if (counter >= DeviceConfig.BEACON_INTERVAL.get() * 2) {
            connect(router);
            return true;
        }
        if (connection != null && connection.getRouterId().equals(router.getId())) {
            connection.setRouterPos(router.getPos());
            counter = 0;
            return true;
        }
        return false;
    }

    public int getSignalStrength() {
        BlockPos routerPos = connection != null ? connection.getRouterPos() : null;
        if (routerPos != null) {
            double distance = Math.sqrt(worldPosition.distSqr(routerPos.getX() + 0.5, routerPos.getY() + 0.5, routerPos.getZ() + 0.5, true));
            double level = DeviceConfig.SIGNAL_RANGE.get() / 3d;
            return distance > level * 2 ? 2 : distance > level ? 1 : 0;
        }
        return -1;
    }

    @Nullable
    @Override
    public TextComponent getDisplayName() {
        return new StringTextComponent(getCustomName());
    }

    @Override
    public @NotNull CompoundNBT save(@NotNull CompoundNBT tag) {
        super.save(tag);
        if (connection != null) {
            tag.put("connection", connection.toTag());
        }
        return tag;
    }

    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains("connection", Constants.NBT.TAG_COMPOUND)) {
            connection = NetworkManager.fromTag(tag.getCompound("connection"));
        }
    }

    public static abstract class Colored extends NetworkDeviceBlockEntity implements Colorable {
        private DyeColor color = DyeColor.RED;

        public Colored(TileEntityType<?> pType) {
            super(pType);
        }

        @Override
        public void load(@NotNull BlockState state, @NotNull CompoundNBT tag) {
            super.load(state, tag);
            if (tag.contains("color", Constants.NBT.TAG_STRING)) {
                color = DyeColor.byId(tag.getByte("color"));
            }
        }

        @Override
        public @NotNull CompoundNBT save(@NotNull CompoundNBT tag) {
            super.save(tag);
            tag.putByte("color", (byte) color.getId());
            return tag;
        }

        @Override
        public CompoundNBT saveSyncTag() {
            CompoundNBT tag = super.saveSyncTag();
            tag.putByte("color", (byte) color.getId());
            return tag;
        }

        @Override
        public void setColor(DyeColor color) {
            this.color = color;
        }

        @Override
        public DyeColor getColor() {
            return color;
        }
    }
}
