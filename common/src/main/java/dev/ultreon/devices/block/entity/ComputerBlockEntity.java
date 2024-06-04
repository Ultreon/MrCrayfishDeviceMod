package dev.ultreon.devices.block.entity;

import com.google.common.collect.Lists;
import dev.ultreon.devices.OperatingSystems;
import dev.ultreon.devices.core.DefaultBios;
import dev.ultreon.devices.core.BootLoader;
import dev.ultreon.devices.core.io.FileSystem;
import dev.ultreon.devices.util.BlockEntityUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ComputerBlockEntity extends NetworkDeviceBlockEntity.Colored {
    private final List<BootLoader<?>> bootLoaders = Lists.newArrayList(OperatingSystems.MINE_OS.get());
    private CompoundTag applicationData = new CompoundTag();
    private CompoundTag systemData = new CompoundTag();
    private FileSystem fileSystem;

    private DyeColor externalDriveColor;
    private boolean poweredOn;
    private CompoundTag originalData;
    private DefaultBios bios;

    protected ComputerBlockEntity(BlockEntityType<? extends ComputerBlockEntity> type, BlockPos pWorldPosition, BlockState pBlockState) {
        super(type, pWorldPosition, pBlockState);
    }

    @Override
    public String getDeviceName() {
        return "MineOS";
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);

        if (compound.contains("original_data")) {
            this.originalData = compound.getCompound("original_data");
        } else {
            this.originalData = compound.copy();
        }

        if (compound.contains("file_system")) {
            this.fileSystem = new FileSystem(this, compound.getCompound("file_system"));
        }
        if (compound.contains("external_drive_color", Tag.TAG_BYTE)) {
            this.externalDriveColor = null;
            if (compound.getByte("external_drive_color") != -1) {
                this.externalDriveColor = DyeColor.byId(compound.getByte("external_drive_color"));
            }
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);

        if (originalData != null) {
            compound.put("original_data", originalData);
        }

        if (systemData != null) {
            compound.put("system_data", systemData);
        }

        if (applicationData != null) {
            compound.put("application_data", applicationData);
        }

        if (fileSystem != null) {
            compound.put("file_system", fileSystem.toTag());
        }
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = super.saveSyncTag();
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

    public CompoundTag getApplicationData() {
        return applicationData != null ? applicationData : new CompoundTag();
    }

    public CompoundTag getSystemData() {
        if (systemData == null) {
            systemData = new CompoundTag();
        }
        return systemData;
    }

    public void setSystemData(CompoundTag systemData) {
        this.systemData = systemData;
        setChanged();
        assert level != null;
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    public FileSystem getFileSystem() {
        if (fileSystem == null) {
            fileSystem = new FileSystem(this, new CompoundTag());
        }
        return fileSystem;
    }

    public void setApplicationData(String appId, CompoundTag applicationData) {
        this.applicationData = applicationData;
        setChanged();
        assert level != null;
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);
    }

    @Environment(EnvType.CLIENT)
    public boolean isExternalDriveAttached() {
        return externalDriveColor != null;
    }

    @Environment(EnvType.CLIENT)
    public DyeColor getExternalDriveColor() {
        return externalDriveColor;
    }

    public boolean isPoweredOn() {
        return this.poweredOn;
    }

    public void powerOn() {
        this.poweredOn = true;
        setChanged();
        assert level != null;
        BlockEntityUtil.markBlockForUpdate(level, worldPosition);

        bios = new DefaultBios(this, this.bootLoaders);
        try {
            bios.powerOn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<BootLoader<?>> getBootLoaders() {
        return bootLoaders;
    }

    public DefaultBios getBios() {
        return bios;
    }
}
