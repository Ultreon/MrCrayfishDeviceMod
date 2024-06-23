package com.ultreon.devices.block.entity;

import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.init.DeviceBlockEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.block.BlockState;

import javax.annotation.Nullable;

/**
 * @author MrCrayfish
 */
public class PaperBlockEntity extends SyncBlockEntity {
    private IPrint print;
    private byte rotation;

    public PaperBlockEntity() {
        super(DeviceBlockEntities.PAPER.get());
    }

    public void nextRotation() {
        rotation++;
        if (rotation > 7) {
            rotation = 0;
        }
        pipeline.putByte("rotation", rotation);
        sync();
        playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM);
    }

    public float getRotation() {
        return rotation * 45f;
    }

    @Nullable
    public IPrint getPrint() {
        return print;
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        if (compound.contains("print", Constants.NBT.TAG_COMPOUND)) {
            print = IPrint.load(compound.getCompound("print"));
        }
        if (compound.contains("rotation", Constants.NBT.TAG_BYTE)) {
            rotation = compound.getByte("rotation");
        }
    }

    @Override
    public void save(CompoundNBT compound) {
        super.save(compound);
        if (print != null) {
            compound.put("print", IPrint.save(print));
        }
        compound.putByte("rotation", rotation);
    }

    @Override
    public CompoundNBT saveSyncTag() {
        CompoundNBT tag = new CompoundNBT();
        if (print != null) {
            tag.put("print", IPrint.save(print));
        }
        tag.putByte("rotation", rotation);
        return tag;
    }

    private void playSound(SoundEvent sound) {
        level.playSound(null, worldPosition, sound, SoundCategory.BLOCKS, 1f, 1f);
    }
}
