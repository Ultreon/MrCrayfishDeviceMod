package com.ultreon.devices.block;

import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.item.FlashDriveItem;
import com.ultreon.devices.util.BlockEntityUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class LaptopBlock extends DeviceBlock.Colored {
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    private static final VoxelShape SHAPE_OPEN_NORTH = VoxelShapes.or(Block.box(1, 0, 12.5, 15, 11.4, 17), Block.box(1, 0, 1, 15, 1.3, 12.5));
    private static final VoxelShape SHAPE_OPEN_EAST = VoxelShapes.or(Block.box(-1, 0, 1, 3.5, 11.4, 15), Block.box(3.5, 0, 1, 15, 1.3, 15));
    private static final VoxelShape SHAPE_OPEN_SOUTH = VoxelShapes.or(Block.box(1, 0, -1, 15, 11.4, 3.5), Block.box(1, 0, 3.5, 15, 1.3, 15));
    private static final VoxelShape SHAPE_OPEN_WEST = VoxelShapes.or(Block.box(12.5, 0, 1, 17, 11.4, 15), Block.box(1, 0, 1, 12.5, 1.3, 15));
    private static final VoxelShape SHAPE_CLOSED_NORTH = Block.box(1, 0, 1, 15, 2, 13);
    private static final VoxelShape SHAPE_CLOSED_EAST = Block.box(3, 0, 1, 15, 2, 15);
    private static final VoxelShape SHAPE_CLOSED_SOUTH = Block.box(1, 0, 3, 15, 2, 15);
    private static final VoxelShape SHAPE_CLOSED_WEST = Block.box(1, 0, 1, 13, 2, 15);

    public LaptopBlock(DyeColor color) {
        super(Properties.of(Material.HEAVY_METAL, color).strength(6f).sound(SoundType.METAL), color, ModDeviceTypes.LAPTOP);
        registerDefaultState(this.getStateDefinition().any().setValue(TYPE, Type.BASE).setValue(OPEN, false));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull IBlockReader pLevel, @NotNull BlockPos pPos, @NotNull ISelectionContext pContext) {
        if (pState.getValue(OPEN)) switch (pState.getValue(FACING)) {
            case NORTH:
                return SHAPE_OPEN_NORTH;
            case EAST:
                return SHAPE_OPEN_EAST;
            case SOUTH:
                return SHAPE_OPEN_SOUTH;
            case WEST:
                return SHAPE_OPEN_WEST;
            default:
                throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        }
        switch (pState.getValue(FACING)) {
            case NORTH:
                return SHAPE_CLOSED_NORTH;
            case EAST:
                return SHAPE_CLOSED_EAST;
            case SOUTH:
                return SHAPE_CLOSED_SOUTH;
            case WEST:
                return SHAPE_CLOSED_WEST;
            default:
                throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        }
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(@NotNull BlockState state, @NotNull World level, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult hit) {

        TileEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LaptopBlockEntity) {
            LaptopBlockEntity laptop = (LaptopBlockEntity) blockEntity;
            if (player.isCrouching()) {
                if (!level.isClientSide) {
                    laptop.openClose(player);
                }
                return ActionResultType.SUCCESS;
            } else {
                if (hit.getDirection() == state.getValue(FACING).getCounterClockWise()) {
                    ItemStack heldItem = player.getItemInHand(hand);
                    if (!heldItem.isEmpty() && heldItem.getItem() instanceof FlashDriveItem) {
                        if (!level.isClientSide) {
                            if (laptop.getFileSystem().setAttachedDrive(heldItem.copy())) {
                                heldItem.shrink(1);
                                return ActionResultType.CONSUME;
                            } else {
                                return ActionResultType.FAIL;
                            }
                        }
                        return ActionResultType.PASS;
                    }

                    if (!level.isClientSide) {
                        ItemStack stack = laptop.getFileSystem().removeAttachedDrive();
                        if (stack != null) {
                            BlockPos summonPos = pos.relative(state.getValue(FACING).getCounterClockWise());
                            level.addFreshEntity(new ItemEntity(level, summonPos.getX() + 0.5, summonPos.getY(), summonPos.getZ() + 0.5, stack));
                            BlockEntityUtil.markBlockForUpdate(level, pos);
                        }
                    }
                    return ActionResultType.SUCCESS;
                }

                if (laptop.isOpen() && level.isClientSide) {
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                        ClientLaptopWrapper.execute(laptop);
                    });
                    return ActionResultType.SUCCESS;
                }
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    protected void removeTagsForDrop(CompoundNBT tileEntityTag) {
        tileEntityTag.remove("open");
    }

    @Override
    public @Nullable TileEntity newBlockEntity(@NotNull IBlockReader reader) {
        return new LaptopBlockEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TYPE, OPEN);
    }

    public enum Type implements IStringSerializable {
        BASE, SCREEN;

        @NotNull
        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
