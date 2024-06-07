package dev.ultreon.devices.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FrameBufferTexture extends DynamicTexture {
    private final ResourceLocation location;

    public FrameBufferTexture(ResourceLocation location, int width, int height) {
        super(width, height, true);

        this.location = location;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public void resize(int width, int height) {
        NativeImage nativeImage = new NativeImage(width, height, true);
        NativeImage pixels = this.getPixels();
        if (pixels != null) {
            nativeImage.copyFrom(pixels);
        }
        this.setPixels(nativeImage);
    }

    @Override
    public void setPixels(@NotNull NativeImage pixels) {
        super.setPixels(pixels);
    }

    @Override
    public @NotNull NativeImage getPixels() {
        return Objects.requireNonNull(super.getPixels());
    }
}
