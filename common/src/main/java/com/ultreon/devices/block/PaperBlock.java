package com.ultreon.devices.block;

import com.ultreon.devices.api.print.IPrint;
import com.ultreon.devices.block.entity.PaperBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.block.material.Material;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class PaperBlock extends HorizontalBlock implements ITileEntityProvider {
    private static final VoxelShape SELECTION_BOUNDS = box(15, 0, 0, 16, 16, 16);

    private static final VoxelShape SELECTION_BOX_NORTH = box(15, 0, 0, 16, 16, 16);
    private static final VoxelShape SELECTION_BOX_SOUTH = box(0, 0, 0, 1, 16, 16);
    private static final VoxelShape SELECTION_BOX_WEST = box(0, 0, 15, 16, 16, 16);
    private static final VoxelShape SELECTION_BOX_EAST = box(0, 0, 0, 16, 16, 1);
    private static final VoxelShape[] SELECTION_BOUNDING_BOX = {SELECTION_BOX_SOUTH, SELECTION_BOX_WEST, SELECTION_BOX_NORTH, SELECTION_BOX_EAST};

    public PaperBlock() {
        super(Properties.of(Material.CLOTH_DECORATION).noCollission().instabreak().noOcclusion().noDrops());

        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> SELECTION_BOX_NORTH;
            case SOUTH -> SELECTION_BOX_SOUTH;
            case WEST -> SELECTION_BOX_WEST;
            case EAST -> SELECTION_BOX_EAST;
            default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return state != null ? state.setValue(FACING, pContext.getHorizontalDirection()) : null;
    }

    @Override
    public ActionResultType use(BlockState pState, World level, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockRayTraceResult pHit) {
        if (!level.isClientSide) {
            TileEntity blockEntity = level.getBlockEntity(pPos);
            if (blockEntity instanceof PaperBlockEntity paper) {
                paper.nextRotation();
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootContext.Builder pBuilder) {
        return new ArrayList<>();
    }

    @Override
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClientSide) {
            TileEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof PaperBlockEntity paper) {
                ItemStack drop = IPrint.generateItem(paper.getPrint());
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public boolean triggerEvent(@NotNull BlockState state, World level, @NotNull BlockPos pos, int id, int param) {
        TileEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(id, param);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    // Todo: Port this to Minecraft 1.18.2
//    public EnumBlockRenderType getRenderType(IBlockState state) {
//        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
//    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PaperBlockEntity();
    }
}
