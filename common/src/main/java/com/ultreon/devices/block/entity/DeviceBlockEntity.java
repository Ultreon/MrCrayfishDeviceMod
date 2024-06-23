package com.ultreon.devices.block.entity;

import com.ultreon.devices.block.DeviceBlock;
import com.ultreon.devices.util.Colorable;
import com.ultreon.devices.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.text.Component;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.item.DyeColor;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class DeviceBlockEntity extends SyncBlockEntity implements Tickable {
    private DyeColor color = DyeColor.RED;
    private UUID deviceId;
    private String name;

    public DeviceBlockEntity(TileEntityType<?> pType) {
        super(pType);
    }

    @NotNull
    public final UUID getId() {
        if (deviceId == null) {
            deviceId = UUID.randomUUID();
        }
        return deviceId;
    }

    public abstract String getDeviceName();

    public String getCustomName() {
        return hasCustomName() ? name : getDeviceName();
    }

    public void setCustomName(String name) {
        this.name = name;
    }

    public boolean hasCustomName() {
        return name != null && StringUtils.isEmpty(name);
    }

    public TextComponent getDisplayName() {
        return new StringTextComponent(getCustomName());
    }

    @Override
    protected void save(BlockState state, @NotNull CompoundNBT tag) {
        super.save(tag);

        tag.putString("deviceId", getId().toString());
        if (hasCustomName()) {
            tag.putString("name", name);
        }

        tag.putByte("color", (byte) color.getId());
    }

    @Override
    public void load(BlockState state, @NotNull CompoundNBT tag) {
        super.load(state, tag);

        if (tag.contains("deviceId", Constants.NBT.TAG_STRING)) {
            deviceId = UUID.fromString(tag.getString("deviceId"));
        }
        if (tag.contains("name", Constants.NBT.TAG_STRING)) {
            name = tag.getString("name");
        }
        if (tag.contains("color", Constants.NBT.TAG_BYTE)) {
            color = DyeColor.byId(tag.getByte("color"));
        }
    }

    @Override
    public CompoundNBT saveSyncTag() {
        CompoundNBT tag = new CompoundNBT();
        if (hasCustomName()) {
            tag.putString("name", name);
        }

        tag.putByte("color", (byte) color.getId());

        return tag;
    }

    public Block getBlock() {
        return getBlockState().getBlock();
    }

    public DeviceBlock getDeviceBlock() {
        Block block = getBlockState().getBlock();
        if (block instanceof DeviceBlock deviceBlock) {
            return deviceBlock;
        }
        return null;
    }

    public static abstract class Colored extends DeviceBlockEntity implements Colorable {
        private DyeColor color = DyeColor.RED;

        public Colored(TileEntityType<?> pType) {
            super(pType);
        }

        @Override
        public void load(BlockState state, @NotNull CompoundNBT tag) {
            super.load(state, tag);
            if (tag.contains("color", Constants.NBT.TAG_BYTE)) {
                color = DyeColor.byId(tag.getByte("color"));
            }
        }

        @Override
        protected void save(BlockState state, @NotNull CompoundNBT tag) {
            super.save(tag);
            tag.putByte("color", (byte) color.getId());
        }

        @Override
        public CompoundNBT saveSyncTag() {
            CompoundNBT tag = super.saveSyncTag();
            tag.putByte("color", (byte) color.getId());
            return tag;
        }

        public DyeColor getColor() {
            return color;
        }

        public void setColor(DyeColor color) {
            this.color = color;
        }
    }
}
