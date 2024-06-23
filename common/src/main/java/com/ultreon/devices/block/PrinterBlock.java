package com.ultreon.devices.block;

import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.block.entity.PrinterBlockEntity;
import com.ultreon.devices.util.Colored;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.SoundType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author MrCrayfish
 */
public class PrinterBlock extends DeviceBlock.Colored implements Colored {
    private static final VoxelShape SHAPE_NORTH = VoxelShapes.or(
            box(2, 0, 7, 14, 5, 12),
            box(3.5, 0.1, 1, 12.5, 1.1, 7),
            box(12, 0, 12, 15, 5, 14),
            box(12, 0, 5, 15, 3, 7),
            box(1, 0, 5, 4, 3, 7),
            box(1, 0, 12, 4, 5, 14),
            box(1.1, 0, 7, 14.9, 5, 12),
            box(4, 0, 12, 12, 3, 14),
            box(3.5, 0.1, 1, 12.5, 1.1, 7.5),
            box(1, 3, 5, 15, 5, 7),
            box(4, 3, 12, 12, 9.3, 16));
    private static final VoxelShape SHAPE_EAST = VoxelShapes.or(
            box(4, 0, 2, 9, 5, 14),
            box(9, 0.1, 3.5, 15, 1.1, 12.5),
            box(2, 0, 12, 4, 5, 15),
            box(9, 0, 12, 11, 3, 15),
            box(9, 0, 1, 11, 3, 4),
            box(2, 0, 1, 4, 5, 4),
            box(4, 0, 1.1, 9, 5, 14.9),
            box(2, 0, 4, 4, 3, 12),
            box(8.5, 0.1, 3.5, 15, 1.1, 12.5),
            box(9, 3, 1, 11, 5, 15),
            box(0, 3, 4, 4, 9.3, 12));
    private static final VoxelShape SHAPE_SOUTH = VoxelShapes.or(
            box(2, 0, 4, 14, 5, 9),
            box(3.5, 0.1, 9, 12.5, 1.1, 15),
            box(1, 0, 2, 4, 5, 4),
            box(1, 0, 9, 4, 3, 11),
            box(12, 0, 9, 15, 3, 11),
            box(12, 0, 2, 15, 5, 4),
            box(1.1, 0, 4, 14.9, 5, 9),
            box(4, 0, 2, 12, 3, 4),
            box(3.5, 0.1, 8.5, 12.5, 1.1, 15),
            box(1, 3, 9, 15, 5, 11),
            box(4, 3, 0, 12, 9.3, 3.4));
    private static final VoxelShape SHAPE_WEST = VoxelShapes.or(
            box(7, 0, 2, 12, 5, 14),
            box(1, 0.1, 3.5, 7, 1.1, 12.5),
            box(12, 0, 1, 14, 5, 4),
            box(5, 0, 1, 7, 3, 4),
            box(5, 0, 12, 7, 3, 15),
            box(12, 0, 12, 14, 5, 15),
            box(7, 0, 1.1, 12, 5, 14.9),
            box(12, 0, 4, 14, 3, 12),
            box(1, 0.1, 3.5, 7.5, 1.1, 12.5),
            box(5, 3, 1, 7, 5, 15),
            box(12, 3, 4, 16, 9.3, 12));

    public PrinterBlock(DyeColor color) {
        super(Properties.of(Material.HEAVY_METAL, color).strength(6f).sound(SoundType.METAL), color, ModDeviceTypes.PRINTER);
        this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState pState, @NotNull IBlockReader pLevel, @NotNull BlockPos pPos, @NotNull ISelectionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        };
    }

    @NotNull
    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(@NotNull BlockState state, World level, @NotNull BlockPos pos, PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult hit) {
        if (level.isClientSide) {
            if (player.isCrouching()) {
                return ActionResultType.SUCCESS;
            } else {
                return super.use(state, level, pos, player, hand, hit);
            }
        }
        ItemStack heldItem = player.getItemInHand(hand);
        TileEntity tileEntity = level.getChunkAt(pos).getBlockEntity(pos, Chunk.CreateEntityType.IMMEDIATE);
        if (tileEntity instanceof PrinterBlockEntity) {
            return ((PrinterBlockEntity) tileEntity).addPaper(heldItem, player.isCrouching()) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }
        return ActionResultType.PASS;
    }

    @Override
    public @Nullable TileEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PrinterBlockEntity();
    }
}
