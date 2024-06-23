package com.ultreon.devices.item;

import com.ultreon.devices.DeviceConfig;
import com.ultreon.devices.Devices;
import com.ultreon.devices.block.entity.NetworkDeviceBlockEntity;
import com.ultreon.devices.block.entity.RouterBlockEntity;
import com.ultreon.devices.core.network.Router;
import com.ultreon.devices.util.KeyboardHelper;
import net.minecraft.util.text.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author MrCrayfish
 */
public class EthernetCableItem extends Item {
    public EthernetCableItem() {
        super(new Properties().tab(Devices.GROUP_DEVICE).stacksTo(1));
    }

    private static double getDistance(BlockPos source, BlockPos target) {
        return Math.sqrt(source.distSqr(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, false));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World level = context.getLevel();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Hand hand = context.getHand();

        if (!level.isClientSide && player != null) {
            ItemStack heldItem = player.getItemInHand(hand);
            TileEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof RouterBlockEntity) {
                RouterBlockEntity routerBE = (RouterBlockEntity) blockEntity;
                if (!heldItem.hasTag()) {
                    sendGameInfoMessage(player, "message.devices.invalid_cable");
                    return ActionResultType.SUCCESS;
                }

                Router router = routerBE.getRouter();

                CompoundNBT tag = heldItem.getTag();
                assert tag != null;
                BlockPos devicePos = BlockPos.of(tag.getLong("pos"));

                TileEntity tileEntity1 = level.getBlockEntity(devicePos);
                if (tileEntity1 instanceof NetworkDeviceBlockEntity) {
                    NetworkDeviceBlockEntity networkDeviceBlockEntity = (NetworkDeviceBlockEntity) tileEntity1;
                    if (!router.isDeviceRegistered(networkDeviceBlockEntity)) {
                        if (router.addDevice(networkDeviceBlockEntity)) {
                            networkDeviceBlockEntity.connect(router);
                            heldItem.shrink(1);
                            if (getDistance(tileEntity1.getBlockPos(), routerBE.getBlockPos()) > DeviceConfig.SIGNAL_RANGE.get()) {
                                sendGameInfoMessage(player, "message.devices.successful_registered");
                            } else {
                                sendGameInfoMessage(player, "message.devices.successful_connection");
                            }
                        } else {
                            sendGameInfoMessage(player, "message.devices.router_max_devices");
                        }
                    } else {
                        sendGameInfoMessage(player, "message.devices.device_already_connected");
                    }
                } else {
                    if (router.addDevice(tag.getUUID("id"), tag.getString("name"))) {
                        heldItem.shrink(1);
                        sendGameInfoMessage(player, "message.devices.successful_registered");
                    } else {
                        sendGameInfoMessage(player, "message.devices.router_max_devices");
                    }
                }
                return ActionResultType.SUCCESS;
            }

            if (blockEntity instanceof NetworkDeviceBlockEntity) {
                NetworkDeviceBlockEntity networkDeviceBlockEntity = (NetworkDeviceBlockEntity) blockEntity;
                heldItem.setTag(new CompoundNBT());
                CompoundNBT tag = heldItem.getTag();
                assert tag != null;
                tag.putUUID("id", networkDeviceBlockEntity.getId());
                tag.putString("name", networkDeviceBlockEntity.getCustomName());
                tag.putLong("pos", networkDeviceBlockEntity.getBlockPos().asLong());

                sendGameInfoMessage(player, "message.devices.select_router");
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.SUCCESS;
    }

    private void sendGameInfoMessage(PlayerEntity player, String message) {
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            serverPlayer.sendMessage(new TranslationTextComponent(message), ChatType.GAME_INFO, Util.NIL_UUID);
        }
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> use(World level, @NotNull PlayerEntity player, @NotNull Hand usedHand) {
        if (!level.isClientSide) {
            ItemStack heldItem = player.getItemInHand(usedHand);
            if (player.isCrouching()) {
                heldItem.resetHoverName();
                heldItem.setTag(null);
                return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
            }
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable World level, List<net.minecraft.util.text.ITextComponent> tooltip, @NotNull ITooltipFlag isAdvanced) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if (tag != null) {
                tooltip.add(new StringTextComponent(TextFormatting.RED.toString() + TextFormatting.BOLD + "ID: " + TextFormatting.RESET + tag.getUUID("id")));
                tooltip.add(new StringTextComponent(TextFormatting.RED.toString() + TextFormatting.BOLD + "Device: " + TextFormatting.RESET + tag.getString("name")));

                BlockPos devicePos = BlockPos.of(tag.getLong("pos"));
                String text = TextFormatting.RED.toString() + TextFormatting.BOLD + "X: " + TextFormatting.RESET + devicePos.getX() + " " +
                        TextFormatting.RED + TextFormatting.BOLD + "Y: " + TextFormatting.RESET + devicePos.getY() + " " +
                        TextFormatting.RED + TextFormatting.BOLD + "Z: " + TextFormatting.RESET + devicePos.getZ();
                tooltip.add(new StringTextComponent(text));
            }
        } else {
            if (!KeyboardHelper.isShiftDown()) {
                tooltip.add(new StringTextComponent(TextFormatting.GRAY + "Use this cable to connect"));
                tooltip.add(new StringTextComponent(TextFormatting.GRAY + "a device to a router."));
                tooltip.add(new StringTextComponent(TextFormatting.YELLOW + "Hold SHIFT for How-To"));
                return;
            }

            tooltip.add(new StringTextComponent(TextFormatting.GRAY + "Start by right clicking a"));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + "device with this cable"));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + "then right click the "));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + "router you want to"));
            tooltip.add(new StringTextComponent(TextFormatting.GRAY + "connect this device to."));
        }
        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return stack.hasTag();
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        if (stack.hasTag()) {
            return super.getDescription().copy().withStyle(TextFormatting.GRAY, TextFormatting.BOLD);
        }
        return super.getName(stack);
    }
}
