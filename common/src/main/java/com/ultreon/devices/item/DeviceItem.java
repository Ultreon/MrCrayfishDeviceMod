package com.ultreon.devices.item;

import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.IDeviceType;
import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DeviceItem extends BlockItem implements IDeviceType {
    private final ModDeviceTypes deviceType;

    public DeviceItem(Block block, Properties properties, ModDeviceTypes deviceType) {
        super(block, properties.stacksTo(1));
        this.deviceType = deviceType;
    }

    //This method is still bugged due to Forge.
    @Nullable
    @PlatformOnly(PlatformOnly.FORGE)
//    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT tag = new CompoundNBT();
        if (stack.getTag() != null && stack.getTag().contains("display", Constants.NBT.TAG_COMPOUND)) {
            tag.put("display", Objects.requireNonNull(stack.getTag().get("display")));
        }
        return tag;
    }

    public ModDeviceTypes getDeviceType() {
        return deviceType;
    }
}
