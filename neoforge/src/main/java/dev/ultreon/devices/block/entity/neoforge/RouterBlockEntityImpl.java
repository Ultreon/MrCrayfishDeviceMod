package dev.ultreon.devices.block.entity.neoforge;

import net.minecraft.world.phys.AABB;

import static net.neoforged.neoforge.client.extensions.IBlockEntityRendererExtension.INFINITE_EXTENT_AABB;

public class RouterBlockEntityImpl {
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
