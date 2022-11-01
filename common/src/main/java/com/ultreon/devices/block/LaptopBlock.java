package com.ultreon.devices.block;

import com.ultreon.devices.ModDeviceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class LaptopBlock extends ComputerBlock.Colored {
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
    public static final BooleanProperty OPEN = BooleanProperty.create("open");

    private static final VoxelShape SHAPE_OPEN_NORTH = Shapes.or(Block.box(1, 0, 12.5, 15, 11.4, 17), Block.box(1, 0, 1, 15, 1.3, 12.5));
    private static final VoxelShape SHAPE_OPEN_EAST = Shapes.or(Block.box(-1, 0, 1, 3.5, 11.4, 15), Block.box(3.5, 0, 1, 15, 1.3, 15));
    private static final VoxelShape SHAPE_OPEN_SOUTH = Shapes.or(Block.box(1, 0, -1, 15, 11.4, 3.5), Block.box(1, 0, 3.5, 15, 1.3, 15));
    private static final VoxelShape SHAPE_OPEN_WEST = Shapes.or(Block.box(12.5, 0, 1, 17, 11.4, 15), Block.box(1, 0, 1, 12.5, 1.3, 15));
    private static final VoxelShape SHAPE_CLOSED_NORTH = Block.box(1, 0, 1, 15, 2, 13);
    private static final VoxelShape SHAPE_CLOSED_EAST = Block.box(3, 0, 1, 15, 2, 15);
    private static final VoxelShape SHAPE_CLOSED_SOUTH = Block.box(1, 0, 3, 15, 2, 15);
    private static final VoxelShape SHAPE_CLOSED_WEST = Block.box(1, 0, 1, 13, 2, 15);

    public LaptopBlock(DyeColor color) {
        super(Properties.of(Material.HEAVY_METAL, color).strength(6f).sound(SoundType.METAL), color, ModDeviceTypes.COMPUTER);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return pState.getValue(OPEN) ? switch (pState.getValue(FACING)) {
            case NORTH -> SHAPE_OPEN_NORTH;
            case EAST -> SHAPE_OPEN_EAST;
            case SOUTH -> SHAPE_OPEN_SOUTH;
            case WEST -> SHAPE_OPEN_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        } : switch (pState.getValue(FACING)) {
            case NORTH -> SHAPE_CLOSED_NORTH;
            case EAST -> SHAPE_CLOSED_EAST;
            case SOUTH -> SHAPE_CLOSED_SOUTH;
            case WEST -> SHAPE_CLOSED_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + pState.getValue(FACING));
        };
    }

    public boolean isDesktopPC() {
        return false;
    }
}
