package com.ultreon.devices.block;

import com.ultreon.devices.IDeviceType;
import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.block.entity.DeviceBlockEntity;
import com.ultreon.devices.util.Colorable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("deprecation")
public abstract class DeviceBlock extends HorizontalBlock implements ITileEntityProvider, IDeviceType {
    private final ModDeviceTypes deviceType;

    public DeviceBlock(Properties properties, ModDeviceTypes deviceType) {
        super(properties.strength(0.5f));
        this.deviceType = deviceType;
    }

//    @Override
//    public RenderShape getRenderShape(BlockState state) {
//        return RenderShape.INVISIBLE;
//    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState pState, @NotNull IBlockReader pLevel, @NotNull BlockPos pPos, @NotNull ISelectionContext pContext) {
        return VoxelShapes.empty();
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NotNull BlockItemUseContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return state != null ? state.setValue(FACING, Objects.requireNonNull(pContext.getPlayer(), "Player in block placement context is null.").getDirection().getOpposite()) : null;
    }

    @Override
    public void setPlacedBy(@NotNull World level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        TileEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof DeviceBlockEntity) {
            DeviceBlockEntity deviceBlockEntity = (DeviceBlockEntity) blockEntity;
            if (stack.hasCustomHoverName()) {
                deviceBlockEntity.setCustomName(stack.getHoverName().getString());
            }
        }
    }


    @Override
    public void destroy(IWorld level, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (!level.isClientSide()) {
            TileEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof DeviceBlockEntity) {
                DeviceBlockEntity device = (DeviceBlockEntity) blockEntity;
                CompoundNBT blockEntityTag = new CompoundNBT();
                blockEntity.save(blockEntityTag);
                blockEntityTag.remove("id");

                removeTagsForDrop(blockEntityTag);

                CompoundNBT tag = new CompoundNBT();
                tag.put("BlockEntityTag", blockEntityTag);

                ItemStack drop;
                if (blockEntity instanceof Colorable) {
                    drop = new ItemStack(this, 1);
                } else {
                    drop = new ItemStack(this);
                }
                drop.setTag(tag);

                if (device.hasCustomName()) {
                    drop.setHoverName(new StringTextComponent(device.getCustomName()));
                }

                level.addFreshEntity(new ItemEntity((World) level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));

                level.removeBlock(pos, false);
                return;
            }
        }
        super.destroy(level, pos, state);
    }

    protected void removeTagsForDrop(CompoundNBT blockEntityTag) {

    }

    @Nullable
    @Override
    public abstract TileEntity newBlockEntity(@NotNull IBlockReader reader);

    @Override
    public boolean triggerEvent(@NotNull BlockState state, World level, @NotNull BlockPos pos, int id, int param) {
        TileEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(id, param);
    }

    @Override
    public ModDeviceTypes getDeviceType() {
        return deviceType;
    }

    public static abstract class Colored extends DeviceBlock {
        private final DyeColor color;

        protected Colored(Properties properties, DyeColor color, ModDeviceTypes deviceType) {
            super(properties, deviceType);
            this.color = color;
        }

        public DyeColor getColor() {
            return color;
        }

        @Override
        public void setPlacedBy(@NotNull World level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
            super.setPlacedBy(level, pos, state, placer, stack);
            TileEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof Colorable) {
                Colorable colored = (Colorable) blockEntity;
                colored.setColor(color);
            }
        }

        // Todo - Implement onDestroyedByPlayer if colored, and needed to implement it. Needs to check if it works without it.


        @Override
        protected void createBlockStateDefinition(StateContainer.@NotNull Builder<Block, BlockState> pBuilder) {
            super.createBlockStateDefinition(pBuilder);
            pBuilder.add(FACING);
        }
    }
}
