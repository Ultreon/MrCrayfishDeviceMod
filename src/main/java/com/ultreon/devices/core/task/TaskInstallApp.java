package com.ultreon.devices.core.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.object.AppInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;

/**
 * @author MrCrayfish
 */
public class TaskInstallApp extends Task {
    private String appId;
    private BlockPos laptopPos;
    private boolean install;

    public TaskInstallApp() {
        super("install_app");
    }

    public TaskInstallApp(AppInfo info, BlockPos laptopPos, boolean install) {
        this();
        this.appId = info.getFormattedId();
        this.laptopPos = laptopPos;
        this.install = install;
    }

    @Override
    public void prepareRequest(CompoundNBT tag) {
        tag.putString("appId", appId);
        tag.putLong("pos", laptopPos.asLong());
        tag.putBoolean("install", install);
        System.out.println("Prep message " + appId + ", " + laptopPos.toString() + ", " + install);
    }

    @Override
    public void processRequest(CompoundNBT tag, World level, PlayerEntity player) {
        System.out.println("Proc message " + tag.getString("appId") + ", " +  BlockPos.of(tag.getLong("pos")) + ", " + tag.getBoolean("install"));
        String appId = tag.getString("appId");
        System.out.println(level.getBlockState(BlockPos.of(tag.getLong("pos"))).getBlock().toString());
        TileEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos"))).getBlockEntity(BlockPos.of(tag.getLong("pos")), Chunk.CreateEntityType.IMMEDIATE);
        System.out.println(tileEntity);
        if (tileEntity instanceof LaptopBlockEntity) {
            LaptopBlockEntity laptop = (LaptopBlockEntity) tileEntity;
            System.out.println("laptop is made out of laptop");
            CompoundNBT systemData = laptop.getSystemData();
            ListNBT list = systemData.getList("InstalledApps", Constants.NBT.TAG_STRING);

            if (tag.getBoolean("install")) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.getString(i).equals(appId)) {
                        System.out.println("FOund duplicate, noping out");
                        return;
                    }
                }
                list.add(StringNBT.valueOf(appId));
                this.setSuccessful();
            } else {
                list.removeIf(appTag -> {
                    if (appTag.getAsString().equals(appId)) {
                        this.setSuccessful();
                        return true;
                    } else {
                        return false;
                    }
                });
            }
            systemData.put("InstalledApps", list);
        }
        System.out.println("Successful: " + this.isSucessful());
    }


    @Override
    public void prepareResponse(CompoundNBT tag) {

    }

    @Override
    public void processResponse(CompoundNBT tag) {

    }
}
