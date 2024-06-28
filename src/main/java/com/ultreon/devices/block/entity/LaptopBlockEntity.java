package com.ultreon.devices.block.entity;

import com.ultreon.devices.block.LaptopBlock;
import com.ultreon.devices.core.io.FileSystem;
import com.ultreon.devices.init.DeviceBlockEntities;
import com.ultreon.devices.util.BlockEntityUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LaptopBlockEntity extends NetworkDeviceBlockEntity.Colored {
    private static final int OPENED_ANGLE = 102;

    private boolean open = false;

    private CompoundNBT applicationData = new CompoundNBT();
    private CompoundNBT systemData = new CompoundNBT();
    private FileSystem fileSystem;

    @OnlyIn(Dist.CLIENT)
    private int rotation;

    @OnlyIn(Dist.CLIENT)
    private int prevRotation;

    private DyeColor externalDriveColor;

    public LaptopBlockEntity() {
        super(DeviceBlockEntities.LAPTOP.get());
    }

    @Override
    public String getDeviceName() {
        return "Laptop";
    }

    @Override
    public void tick() {
        super.tick();
        assert level != null;
        if (level.isClientSide) {
            prevRotation = rotation;
            if (!open) {
                if (rotation > 0) {
                    rotation -= 10F;
                }
            } else {
                if (rotation < OPENED_ANGLE) {
                    rotation += 10F;
                }
            }
        }
    }

    @Override
    public void load(@NotNull BlockState state, @NotNull CompoundNBT compound) {
        super.load(state, compound);
        if (compound.contains("open")) {
            this.open = compound.getBoolean("open");
            this.getBlockState().setValue(LaptopBlock.OPEN, open);
        }
        if (compound.contains("system_data", Constants.NBT.TAG_COMPOUND)) {
            this.systemData = compound.getCompound("system_data");
        }
        if (compound.contains("application_data", Constants.NBT.TAG_COMPOUND)) {
            this.applicationData = compound.getCompound("application_data");
        }
        if (compound.contains("file_system")) {
            this.fileSystem = new FileSystem(this, compound.getCompound("file_system"));
        }
        if (compound.contains("external_drive_color", Constants.NBT.TAG_BYTE)) {
            this.externalDriveColor = null;
            if (compound.getByte("external_drive_color") != -1) {
                this.externalDriveColor = DyeColor.byId(compound.getByte("external_drive_color"));
            }
        }
    }

    @Override
    public @NotNull CompoundNBT save(@NotNull CompoundNBT compound) {
        super.save(compound);
        compound.putBoolean("open", open);

        if (systemData != null) {
            compound.put("system_data", systemData);
        }

        if (applicationData != null) {
            compound.put("application_data", applicationData);
        }

        if (fileSystem != null) {
            compound.put("file_system", fileSystem.toTag());
        }
        return compound;
    }

    @Override
    public CompoundNBT saveSyncTag() {
        CompoundNBT tag = super.saveSyncTag();
        tag.putBoolean("open", open);
        tag.put("system_data", getSystemData());

        if (getFileSystem().getAttachedDrive() != null) {
            tag.putByte("external_drive_color", (byte) getFileSystem().getAttachedDriveColor().getId());
        } else {
            tag.putByte("external_drive_color", (byte) -1);
        }

        return tag;
    }

    // Todo: Port to 1.18.2 if possible
//    @Override
//    public double getMaxRenderDistanceSquared() {
//        return 16384;
//    }
//
//    public AxisAlignedBB getRenderBoundingBox() {
//        return INFINITE_EXTENT_AABB;
//    }

    public void openClose(@Nullable Entity entity) {
        World level = this.level;
        boolean oldOpen = open;
        open = !getBlockState().getValue(LaptopBlock.OPEN);
        if (oldOpen != open) {
            pipeline.putBoolean("open", open);
            BlockState d = getBlockState().setValue(LaptopBlock.OPEN, open);
            this.level.setBlock(this.getBlockPos(), d, 18);
            sync();
        }

        if (level != null) {
            markUpdated();
            doNeighborUpdates(level, this.getBlockPos(), this.getBlockState());
        }
    }

    private static void doNeighborUpdates(World level, BlockPos pos, BlockState state) {
        state.updateNeighbourShapes(level, pos, 3);
    }

    public boolean isOpen() {
        return open;
    }

    public CompoundNBT getApplicationData() {
        return applicationData != null ? applicationData : new CompoundNBT();
    }

    public CompoundNBT getSystemData() {
        if (systemData == null) {
            systemData = new CompoundNBT();
        }
        return systemData;
    }

    public void setSystemData(CompoundNBT systemData) {
        this.systemData = systemData;
        setChanged();
        assert level != null;
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    public FileSystem getFileSystem() {
        if (fileSystem == null) {
            fileSystem = new FileSystem(this, new CompoundNBT());
        }
        return fileSystem;
    }

    public void setApplicationData(String appId, CompoundNBT applicationData) {
        this.applicationData = applicationData;
        setChanged();
        assert level != null;
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    @OnlyIn(Dist.CLIENT)
    public float getScreenAngle(float partialTicks) {
        return -OPENED_ANGLE * ((prevRotation + (rotation - prevRotation) * partialTicks) / OPENED_ANGLE);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isExternalDriveAttached() {
        return externalDriveColor != null;
    }

    @OnlyIn(Dist.CLIENT)
    public DyeColor getExternalDriveColor() {
        return externalDriveColor;
    }
}
