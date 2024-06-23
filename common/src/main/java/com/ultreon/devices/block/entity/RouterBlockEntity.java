package com.ultreon.devices.block.entity;

import com.ultreon.devices.core.network.Router;
import com.ultreon.devices.init.DeviceBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.block.BlockState;

@SuppressWarnings("unused")
public class RouterBlockEntity extends DeviceBlockEntity.Colored {
    private Router router;

    @OnlyIn(Dist.CLIENT)
    private int debugTimer;

    public RouterBlockEntity() {
        super(DeviceBlockEntities.ROUTER.get());
    }

    public Router getRouter() {
        if (router == null) {
            router = new Router(worldPosition);
            setChanged();
        }
        return router;
    }

    public void tick() {
        assert level != null;
        if (!level.isClientSide) {
            getRouter().tick(level);
        } else if (debugTimer > 0) {
            debugTimer--;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isDebug() {
        return debugTimer > 0;
    }

    @OnlyIn(Dist.CLIENT)
    public void setDebug(boolean debug) {
        if (debug) {
            debugTimer = 1200;
        } else {
            debugTimer = 0;
        }
    }

    public String getDeviceName() {
        return "Router";
    }

    @Override
    public void save(CompoundNBT tag) {
        super.save(tag);
        if (tag.contains("router", Constants.NBT.TAG_COMPOUND)) {
            router = Router.fromTag(worldPosition, tag.getCompound("router"));
        }
    }

    public void syncDevicesToClient() {
        pipeline.put("router", getRouter().toTag(true));
        sync();
    }

    // Todo - Maybe implement this whenever possible?
//    @Override
//    public double getMaxRenderDistanceSqr() {
//        return 16384;
//    }
//
//    @PlatformOnly("forge")
//    @Environment(EnvType.CLIENT)
//    @ExpectPlatform
//    public AABB getRenderBoundingBox() {
//        throw new AssertionError();
//    }
}
