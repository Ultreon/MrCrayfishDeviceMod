package com.ultreon.devices.core.task;

import com.ultreon.devices.api.task.Task;
import com.ultreon.devices.block.entity.LaptopBlockEntity;
import com.ultreon.devices.object.AppInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

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
    public void prepareRequest(CompoundTag tag) {
        tag.putString("appId", appId);
        tag.putLong("pos", laptopPos.asLong());
        tag.putBoolean("install", install);
        System.out.println("Prep message " + appId + ", " + laptopPos.toString() + ", " + install);
    }

    @Override
    public void processRequest(CompoundTag tag, Level level, Player player) {
        System.out.println("Proc message " + tag.getString("appId") + ", " +  BlockPos.of(tag.getLong("pos")) + ", " + tag.getBoolean("install"));
        String appId = tag.getString("appId");
        System.out.println(level.getBlockState(BlockPos.of(tag.getLong("pos"))).getBlock().toString());
        BlockEntity tileEntity = level.getChunkAt(BlockPos.of(tag.getLong("pos"))).getBlockEntity(BlockPos.of(tag.getLong("pos")), LevelChunk.EntityCreationType.IMMEDIATE);
        System.out.println(tileEntity);
        if (tileEntity instanceof LaptopBlockEntity laptop) {
            System.out.println("laptop is made out of laptop");
            CompoundTag systemData = laptop.getSystemData();
            ListTag list = systemData.getList("InstalledApps", Tag.TAG_STRING);

            if (tag.getBoolean("install")) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.getString(i).equals(appId)) {
                        System.out.println("FOund duplicate, noping out");
                        return;
                    }
                }
                list.add(StringTag.valueOf(appId));
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
    public void prepareResponse(CompoundTag tag) {

    }

    @Override
    public void processResponse(CompoundTag tag) {

    }
}
