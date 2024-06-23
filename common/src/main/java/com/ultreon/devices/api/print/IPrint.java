package com.ultreon.devices.api.print;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.ultreon.devices.init.DeviceBlocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

//printing somethings takes makes ink cartridge take damage. cartridge can only stack to one

/**
 * @author MrCrayfish
 */
public interface IPrint {
    static CompoundNBT save(IPrint print) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("type", PrintingManager.getPrintIdentifier(print));
        tag.put("data", print.toTag());
        return tag;
    }

    @Nullable
    static IPrint load(CompoundNBT tag) {
        IPrint print = PrintingManager.getPrint(tag.getString("type"));
        if (print != null) {
            print.fromTag(tag.getCompound("data"));
            return print;
        }
        return null;
    }

    static ItemStack generateItem(IPrint print) {
        CompoundNBT blockEntityTag = new CompoundNBT();
        blockEntityTag.put("print", save(print));

        CompoundNBT itemTag = new CompoundNBT();
        itemTag.put("BlockEntityTag", blockEntityTag);

        ItemStack stack = new ItemStack(DeviceBlocks.PAPER.get());
        stack.setTag(itemTag);

        if (print.getName() != null && !print.getName().isEmpty()) {
            stack.setHoverName(new StringTextComponent(print.getName()));
        }
        return stack;
    }

    String getName();

    /**
     * Gets the speed of the print. The higher the value, the longer it will take to print.
     *
     * @return the speed of this print
     */
    int speed();

    /**
     * Gets whether or not this print requires colored ink.
     *
     * @return if print requires ink
     */
    boolean requiresColor();

    /**
     * Converts print into an NBT tag compound. Used for the renderer.
     *
     * @return nbt form of print
     */
    CompoundNBT toTag();

    void fromTag(CompoundNBT tag);

    @OnlyIn(Dist.CLIENT)
    Class<? extends Renderer> getRenderer();

    interface Renderer {
        boolean render(MatrixStack pose, CompoundNBT data);
    }
}
