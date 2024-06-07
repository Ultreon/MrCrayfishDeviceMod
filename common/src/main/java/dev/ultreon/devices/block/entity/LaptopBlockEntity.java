package dev.ultreon.devices.block.entity;

import dev.ultreon.devices.block.LaptopBlock;
import dev.ultreon.devices.init.DeviceBlockEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LaptopBlockEntity extends ComputerBlockEntity {
    private static final int OPENED_ANGLE = 102;

    private boolean open = false;

    @Environment(EnvType.CLIENT)
    private int rotation;

    @Environment(EnvType.CLIENT)
    private int prevRotation;

    private int attachmentCooldown;

    public LaptopBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(DeviceBlockEntities.LAPTOP.get(), pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        super.tick();

        if (getBlockState().getValue(LaptopBlock.OPEN) != open) {
            if (level != null) {
                level.setBlock(getBlockPos(), this.getBlockState().setValue(LaptopBlock.OPEN, open), 2);
            }
        }

        if (level != null && level.isClientSide) {
            prevRotation = rotation;
            if (!open) {
                if (rotation > 0) {
                    rotation -= 10;
                }
            } else {
                if (rotation < OPENED_ANGLE) {
                    rotation += 10;
                }
            }
        }

        if (attachmentCooldown > 0) {
            attachmentCooldown--;
        }
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        if (compound.contains("open")) {
            this.open = compound.getBoolean("open");
            Level level = getLevel();
            if (level != null) {
                if (level.isClientSide) {
                    if (open) this.powerOn();
                }
                level.setBlock(getBlockPos(), this.getBlockState().setValue(LaptopBlock.OPEN, open), 2);
            }
        }

        super.load(compound);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        compound.putBoolean("open", open);

        super.saveAdditional(compound);
    }

    @Override
    public CompoundTag saveSyncTag() {
        CompoundTag tag = super.saveSyncTag();
        tag.putBoolean("open", open);

        return tag;
    }

    public void openClose(@Nullable Entity entity) {
        Level level = this.level;
        if (level != null) {
            level.gameEvent(!open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
        }
        boolean oldOpen = open;
        open = !getBlockState().getValue(LaptopBlock.OPEN);
        if (oldOpen != open) {
            pipeline.putBoolean("open", open);
            var d = getBlockState().setValue(LaptopBlock.OPEN, open);
            this.level.setBlock(this.getBlockPos(), d, 18);
            sync();
        }

        if (level != null) {
            markUpdated();
            doNeighborUpdates(level, this.getBlockPos(), this.getBlockState());
        }
    }

    private static void doNeighborUpdates(Level level, BlockPos pos, BlockState state) {
        state.updateNeighbourShapes(level, pos, 3);
    }

    public boolean isOpen() {
        return open;
    }

    @Environment(EnvType.CLIENT)
    public float getScreenAngle(float partialTicks) {
        return -OPENED_ANGLE * ((prevRotation + (rotation - prevRotation) * partialTicks) / OPENED_ANGLE);
    }

    public void setAttachmentCooldown(int ticks) {
        this.attachmentCooldown = Math.max(ticks, 0);
    }

    public boolean canChangeAttachment() {
        return this.attachmentCooldown <= 0;
    }
}
