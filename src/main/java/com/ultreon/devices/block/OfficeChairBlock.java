package com.ultreon.devices.block;

import com.ultreon.devices.ModDeviceTypes;
import com.ultreon.devices.block.entity.OfficeChairBlockEntity;
import com.ultreon.devices.entity.SeatEntity;
import com.ultreon.devices.util.SeatUtil;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class OfficeChairBlock extends DeviceBlock.Colored
{
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final VoxelShape EMPTY_BOX = VoxelShapes.box(0, 0, 0, 0, 0, 0);
    private static final VoxelShape SELECTION_BOX = VoxelShapes.box(0.0625f, 0, 0.0625f, 0.9375f, /*1.6875f*/0.625f, 0.9375f);
    private static final VoxelShape SEAT_BOUNDING_BOX = VoxelShapes.box(0.0625f, 0, 0.0625f, 0.9375f, 0.625f, 0.9375f);

    public OfficeChairBlock(DyeColor color)
    {
        super(AbstractBlock.Properties.of(Material.STONE, color.getMaterialColor()), color, ModDeviceTypes.SEAT);
        //this.setUnlocalizedName("office_chair");
        //this.setRegistryName("office_chair");
        //this.setCreativeTab(MrCrayfishDeviceMod.GROUP_DEVICE);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.LEGS));
    }

    @Override
    public @NotNull String getDescriptionId() {
        return Util.makeDescriptionId("block", new ResourceLocation("devices", "office_chair"));
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull IWorldReader reader, @NotNull BlockPos pos)
    {
        return false;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull IBlockReader pLevel, @NotNull BlockPos pPos, @NotNull ISelectionContext pContext) {
        return SELECTION_BOX;
    }

    @Override
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull IBlockReader level, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
        return SEAT_BOUNDING_BOX;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull IBlockReader level, @NotNull BlockPos pos, @NotNull ISelectionContext context) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().player.getVehicle() instanceof SeatEntity)
        {
            return EMPTY_BOX;
        }
        return SELECTION_BOX;
    }

    @Override
    public @NotNull ActionResultType use(@NotNull BlockState state, World level, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockRayTraceResult hit)
    {
        //System.out.println(DeviceEntities.SEAT.get().create(level).toString());
        System.out.println("OKOKJRTKFD");
        if(!level.isClientSide)
        {
            SeatUtil.createSeatAndSit(level, pos, player, -1);
        }
        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(@NotNull IBlockReader reader)
    {
        return new OfficeChairBlockEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.@NotNull Builder<Block, BlockState> pBuilder)
    {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TYPE);
    }

    public enum Type implements IStringSerializable
    {
        LEGS, SEAT, FULL;

        @Override
        public @NotNull String getSerializedName()
        {
            return name().toLowerCase();
        }
    }
}