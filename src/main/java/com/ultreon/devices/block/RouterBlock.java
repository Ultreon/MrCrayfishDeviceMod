package com.ultreon.devices.block;

import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.network.PacketHandler;
import com.ultreon.devices.network.task.SyncBlockPacket;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @author MrCrayfish
 */
public class RouterBlock extends DeviceBlock.Colored {
    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");

    // Todo - do rotations for voxel shapes properly.
    private static final VoxelShape[] BODY_BOUNDING_BOX = {
            box(4, 0, 2, 12, 2, 14),
            box(4, 0, 2, 12, 2, 14),
            box(4, 0, 2, 12, 2, 14),
            box(4, 0, 2, 12, 2, 14)
    };
    private static final VoxelShape[] BODY_VERTICAL_BOUNDING_BOX = {
            box(14, 1, 2, 16, 9, 14),
            box(14, 1, 2, 16, 9, 14),
            box(14, 1, 2, 16, 9, 14),
            box(14, 1, 2, 16, 9, 14)
    };
    private static final VoxelShape[] SELECTION_BOUNDING_BOX = {
            box(3, 0, 1, 13, 3, 15),
            box(3, 0, 1, 13, 3, 15),
            box(3, 0, 1, 13, 3, 15),
            box(3, 0, 1, 13, 3, 15)
    };
    private static final VoxelShape[] SELECTION_VERTICAL_BOUNDING_BOX = {
            box(13, 0, 1, 16, 10, 15),
            box(13, 0, 1, 16, 10, 15),
            box(13, 0, 1, 16, 10, 15),
            box(13, 0, 1, 16, 10, 15)
    };

    public RouterBlock(DyeColor color) {
        super(Properties.of(Material.HEAVY_METAL).strength(6f).sound(SoundType.METAL), color, ModDeviceTypes.ROUTER);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(VERTICAL, false));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull IBlockReader pLevel, @NotNull BlockPos pPos, @NotNull ISelectionContext pContext) {
        switch (pState.getValue(FACING)) {
            case NORTH:
                return pState.getValue(VERTICAL) ? BODY_VERTICAL_BOUNDING_BOX[0] : BODY_BOUNDING_BOX[0];
            case EAST:
                return pState.getValue(VERTICAL) ? BODY_VERTICAL_BOUNDING_BOX[1] : BODY_BOUNDING_BOX[1];
            case SOUTH:
                return pState.getValue(VERTICAL) ? BODY_VERTICAL_BOUNDING_BOX[2] : BODY_BOUNDING_BOX[2];
            case WEST:
                return pState.getValue(VERTICAL) ? BODY_VERTICAL_BOUNDING_BOX[3] : BODY_BOUNDING_BOX[3];
            default:
                return BODY_BOUNDING_BOX[0];
        }
    }

    @Override
    public @NotNull ActionResultType use(@NotNull BlockState state, World level, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult hit) {
        if (level.isClientSide && player.isCreative()) {
            TileEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof RouterBlockEntity) {
                RouterBlockEntity router = (RouterBlockEntity) blockEntity;
                router.setDebug(true);
                if (router.isDebug()) {
                    PacketHandler.INSTANCE.sendToServer(new SyncBlockPacket(pos));
                }
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public @org.jetbrains.annotations.Nullable BlockState getStateForPlacement(@NotNull BlockItemUseContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return state != null ? state.setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(VERTICAL, pContext.getClickLocation().y - pContext.getClickLocation().y > 0.5) : null;
    }

//    @Override
//    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
//        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
//        return state.withProperty(VERTICAL, facing.getHorizontalIndex() != -1);
//    }

//    @Override
//    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
//        return side != EnumFacing.DOWN;
//    }

    @NotNull
    @Override
    @Contract("_ -> new")
    public TileEntity newBlockEntity(@NotNull IBlockReader reader) {
        return new RouterBlockEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(VERTICAL);
    }
}
