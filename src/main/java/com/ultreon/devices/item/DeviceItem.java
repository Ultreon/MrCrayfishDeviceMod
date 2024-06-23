package com.ultreon.devices.item;

import com.ultreon.devices.IDeviceType;
import com.ultreon.devices.ModDeviceTypes;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DeviceItem extends BlockItem implements IDeviceType {
    private final ModDeviceTypes deviceType;

    public DeviceItem(Block block, Properties properties, ModDeviceTypes deviceType) {
        super(block, properties.stacksTo(1));
        this.deviceType = deviceType;
    }

    //This method is still bugged due to Forge.
    @Override
    @Nullable
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT tag = new CompoundNBT();
        if (stack.getTag() != null && stack.getTag().contains("display", Constants.NBT.TAG_COMPOUND)) {
            tag.put("display", Objects.requireNonNull(stack.getTag().get("display")));
        }
        return tag;
    }

    @Override
    public ModDeviceTypes getDeviceType() {
        return deviceType;
    }
}
