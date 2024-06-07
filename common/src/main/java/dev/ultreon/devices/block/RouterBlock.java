package dev.ultreon.devices.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ultreon.devices.ModDeviceTypes;
import dev.ultreon.devices.block.entity.RouterBlockEntity;
import dev.ultreon.devices.network.PacketHandler;
import dev.ultreon.devices.network.task.SyncBlockPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author MrCrayfish
 */
public class RouterBlock extends DeviceBlock.Colored {
    public static final MapCodec<RouterBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DyeColor.CODEC.fieldOf("color").forGetter(RouterBlock::getColor),
            propertiesCodec()
    ).apply(instance, RouterBlock::new));

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

    public RouterBlock(DyeColor color, Properties properties) {
        super(properties, color, ModDeviceTypes.ROUTER);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(VERTICAL, false));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> pState.getValue(VERTICAL) ? BODY_VERTICAL_BOUNDING_BOX[0] : BODY_BOUNDING_BOX[0];
            case EAST -> pState.getValue(VERTICAL) ? BODY_VERTICAL_BOUNDING_BOX[1] : BODY_BOUNDING_BOX[1];
            case SOUTH -> pState.getValue(VERTICAL) ? BODY_VERTICAL_BOUNDING_BOX[2] : BODY_BOUNDING_BOX[2];
            case WEST -> pState.getValue(VERTICAL) ? BODY_VERTICAL_BOUNDING_BOX[3] : BODY_BOUNDING_BOX[3];
            default -> BODY_BOUNDING_BOX[0];
        };
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide && player.isCreative()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof RouterBlockEntity router) {
                router.setDebug(true);
                if (router.isDebug()) {
                    PacketHandler.INSTANCE.sendToServer(new SyncBlockPacket(pos));
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        return state != null ? state.setValue(FACING, pContext.getHorizontalDirection().getOpposite()).setValue(VERTICAL, pContext.getClickLocation().y - pContext.getClickLocation().y > 0.5) : null;
    }

    @NotNull
    @Override
    @Contract("_, _ -> new")
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new RouterBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(VERTICAL);
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
