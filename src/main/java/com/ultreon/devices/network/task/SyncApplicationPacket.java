package com.ultreon.devices.network.task;

import com.google.common.collect.ImmutableList;
import com.ultreon.devices.Devices;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.network.Packet;
import com.ultreon.devices.object.AppInfo;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraft.network.PacketBuffer;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author MrCrayfish
 */
public class SyncApplicationPacket extends Packet<SyncApplicationPacket> {
    private final List<AppInfo> allowedApps;

    public SyncApplicationPacket(PacketBuffer buf) {
        int size = buf.readInt();
        ImmutableList.Builder<AppInfo> builder = ImmutableList.builder();
        for (int i = 0; i < size; i++) {
            String appId = buf.readUtf();
            AppInfo info = ApplicationManager.getApplication(appId);
            if (info != null) {
                builder.add(info);
            } else {
                Devices.LOGGER.error("Missing application '" + appId + "'");
            }
        }

        allowedApps = builder.build();
    }

    public SyncApplicationPacket(List<AppInfo> allowedApps) {
        this.allowedApps = allowedApps;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeInt(allowedApps.size());
        for (AppInfo appInfo : allowedApps) {
            buf.writeResourceLocation(appInfo.getId());
        }
    }

    @Override
    public boolean onMessage(Supplier<NetworkEvent.Context> ctx) {
        Devices.setAllowedApps(allowedApps);
        return true;
    }
}
