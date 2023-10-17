package com.ultreon.devices.api.app.component;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ultreon.devices.Devices;
import com.ultreon.devices.api.app.Component;
import com.ultreon.devices.api.app.Layout;
import com.ultreon.devices.api.utils.OnlineRequest;
import com.ultreon.devices.api.utils.RenderUtil;
import com.ultreon.devices.cef.BrowserFramework;
import com.ultreon.devices.core.Laptop;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class BrowserRenderer extends Component {
    public static final Map<String, CachedImage> CACHE = new HashMap<>();
    protected CachedImage image;
    protected boolean initialized = false;
    protected boolean drawFull = false;
    protected int imageU, imageV;
    protected int imageWidth, imageHeight;
    protected int sourceWidth, sourceHeight;
    public int componentWidth;
    public int componentHeight;
    private float alpha = 1f;
    private Supplier<ColorSupplier> tint = () -> Util.make(new ColorSupplier(), cs -> {
        cs.r = 255;
        cs.g = 255;
        cs.b = 255;
    });

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setTint(int r, int g, int b) {
        var cs = new ColorSupplier();
        cs.r = r;
        cs.g = g;
        cs.b = b;
        this.setTint(() -> cs);
    }

    public static class ColorSupplier {
        int r;
        int g;
        int b;
    }

    public void setTint(Supplier<ColorSupplier> colorSupplier) {
        this.tint = colorSupplier;
    }

    private boolean hasBorder = false;
    private int borderColor = Color.BLACK.getRGB();
    private int borderThickness = 0;

    public BrowserRenderer(int left, int top, int width, int height) {
        super(left, top);
        this.componentWidth = width;
        this.componentHeight = height;
    }

    /**
     * Creates a new Image using a ResourceLocation. This automatically sets the width and height of
     * the component according to the width and height of the image.
     *
     * @param left        the amount of pixels to be offset from the left
     * @param top         the amount of pixels to be offset from the top
     * @param imageU      the u position on the image resource
     * @param imageV      the v position on the image resource
     * @param imageWidth  the image width
     * @param imageHeight the image height
     */
    public BrowserRenderer(int left, int top, int imageU, int imageV, int imageWidth, int imageHeight) {
        this(left, top, imageWidth, imageHeight, imageU, imageV, imageWidth, imageHeight);
    }

    /**
     * Creates a new Image using a ResourceLocation. This constructor allows the specification of
     * the width and height of the component instead of automatically unlike
     *
     * @param left            the amount of pixels to be offset from the left
     * @param top             the amount of pixels to be offset from the top
     * @param componentWidth  the width of the component
     * @param componentHeight the height of the component
     * @param imageU          the u position on the image resource
     * @param imageV          the v position on the image resource
     * @param imageWidth      the image width
     * @param imageHeight     the image height
     */
    public BrowserRenderer(int left, int top, int componentWidth, int componentHeight, int imageU, int imageV, int imageWidth, int imageHeight) {
        this(left, top, componentWidth, componentHeight, imageU, imageV, imageWidth, imageHeight, 256, 256);
    }

    public BrowserRenderer(int left, int top, int componentWidth, int componentHeight, int imageU, int imageV, int imageWidth, int imageHeight, int sourceWidth, int sourceHeight) {
        super(left, top);
        this.componentWidth = componentWidth;
        this.componentHeight = componentHeight;
        this.imageU = imageU;
        this.imageV = imageV;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
    }

    @Override
    public void init(Layout layout) {
        initialized = true;
    }

    @Override
    public void handleLoad() {

    }

    @Override
    protected void handleUnload() {
        this.initialized = false;
    }

    @Override
    public void render(GuiGraphics gfx, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
        if (this.visible) {
            if (hasBorder) {
                gfx.fill(x, y, x + componentWidth, y + componentHeight, borderColor);
            }

            RenderSystem.setShaderColor(tint.get().r / 255f, tint.get().g / 255f, tint.get().b / 255f, alpha);

            if (image != null && image.textureId != -1) {
                image.restore();

                RenderSystem.setShaderColor(1, 1, 1, alpha);
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, BrowserFramework.RES);

                if (drawFull) {
                    RenderUtil.drawRectWithTexture(BrowserFramework.RES, gfx, x + borderThickness, y + borderThickness, 0, imageU, imageV, componentWidth - borderThickness * 2, componentHeight - borderThickness * 2, 256, 256);
                } else {
                    RenderUtil.drawRectWithTexture(BrowserFramework.RES, gfx, x + borderThickness, y + borderThickness, imageU, imageV, componentWidth - borderThickness * 2, componentHeight - borderThickness * 2, imageWidth, imageHeight, sourceWidth, sourceHeight);
                }
            } else {
                gfx.fill(x + borderThickness, y + borderThickness, x + componentWidth - borderThickness, y + componentHeight - borderThickness, Color.LIGHT_GRAY.getRGB());
            }
            RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        }
    }

    private int _pBorderThickness = 1;

    /**
     * Makes it so the border shows
     *
     * @param show should the border show
     */
    public void setBorderVisible(boolean show) {
        this.hasBorder = show;
        this.borderThickness = show ? _pBorderThickness : 0;
    }

    /**
     * Sets the border color for this component
     *
     * @param color the border color
     */
    private void setBorderColor(Color color) {
        this.borderColor = color.getRGB();
    }

    /**
     * Sets the thickness of the border
     *
     * @param thickness how thick in pixels
     */
    public void setBorderThickness(int thickness) {
        this._pBorderThickness = thickness;
        this.borderThickness = thickness;
    }

    public void setDrawFull(boolean drawFull) {
        this.drawFull = drawFull;
    }

    /**
     * Image Loader
     */
    private static abstract class ImageLoader {
        protected boolean setup = false;

        public final boolean isSetup() {
            return setup;
        }

        protected void setup(BrowserRenderer image) {
            setup = false;
        }

        public abstract CachedImage load(BrowserRenderer image);
    }

    private static class StandardLoader extends ImageLoader {
        private final AbstractTexture texture;
        private final ResourceLocation resource;

        public StandardLoader(ResourceLocation resource) {
            this.texture = new SimpleTexture(resource);
            this.resource = resource;
        }

        @Override
        protected void setup(BrowserRenderer image) {
            setup = true;
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public CachedImage load(BrowserRenderer image) {
            @Nullable AbstractTexture textureObj = Minecraft.getInstance().getTextureManager().getTexture(resource, null);
            if (textureObj != null) {
                return new CachedImage(textureObj.getId(), 0, 0, false);
            } else {
                AbstractTexture texture = new SimpleTexture(resource);
                Minecraft.getInstance().getTextureManager().register(resource, texture);
                return new CachedImage(texture.getId(), 0, 0, false);
            }
        }

        public AbstractTexture getTexture() {
            return texture;
        }
    }

    private static class DynamicLoader extends ImageLoader {
        private final String url;
        private AbstractTexture texture;

        public DynamicLoader(String url) {
            this.url = url;
        }

        @Override
        public void setup(final BrowserRenderer image) {
            if (CACHE.containsKey(url)) {
                setup = true;
                return;
            }
            Runnable r = () -> {
                try {
                    URL url = new URL(this.url);
                    OnlineRequest.checkURLForSuspicions(url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    InputStream connIn = conn.getInputStream();
                    byte[] bytes = connIn.readAllBytes();
                    connIn.close();
                    conn.disconnect();

                    ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                    ByteArrayInputStream imageIn = new ByteArrayInputStream(bytes);
                    BufferedImage img = ImageIO.read(imageIn);

                    NativeImage nativeImage = NativeImage.read(in);

                    Laptop.runLater(() -> {
                        Devices.LOGGER.debug("Loaded image: " + url);
                        texture = new DynamicTexture(nativeImage);
                        setup = true;
                    });
                } catch (IOException e) {
                    texture = MissingTextureAtlasSprite.getTexture();
                    setup = true;
                    Devices.LOGGER.warn("Failed to load browser frame:", e);
                }
            };
            Thread thread = new Thread(r, "Image Loader");
            thread.start();
        }

        @Override
        public CachedImage load(BrowserRenderer image) {
            if (CACHE.containsKey(url)) {
                CachedImage cachedImage = CACHE.get(url);
                image.imageWidth = cachedImage.width;
                image.imageHeight = cachedImage.height;
                return cachedImage;
            }

            try {
                texture.load(Minecraft.getInstance().getResourceManager());
                CachedImage cachedImage = new CachedImage(texture.getId(), image.imageWidth, image.imageHeight, true);
                if (texture != MissingTextureAtlasSprite.getTexture())
                    CACHE.put(url, cachedImage);

                return cachedImage;
            } catch (IOException e) {
                return new CachedImage(MissingTextureAtlasSprite.getTexture().getId(), 0, 0, true);
            }
        }
    }

    private static class DynamicLoadedTexture extends AbstractTexture {
        private final InputStream in;
        private final BufferedImage image;

        private DynamicLoadedTexture(InputStream in, BufferedImage image) {
            this.in = in;

            this.image = image;
            TextureUtil.prepareImage(getId(), this.image.getWidth(), this.image.getHeight());
        }

        @Override
        public void load(@NotNull ResourceManager resourceManager) throws IOException {
            NativeImage nativeImage = NativeImage.read(in);
            Minecraft.getInstance().getTextureManager().register(Devices.id("dynamic_loaded/" + getId()), this);
            this.upload(nativeImage);
        }

        private void upload(NativeImage nativeImage) {
            nativeImage.upload(0, 0, 0, mipmap);
        }

        public BufferedImage getImage() {
            return image;
        }
    }

    private static class ImageCache extends LinkedHashMap<String, CachedImage> {
        private final int CAPACITY;

        private ImageCache(final int capacity) {
            super(capacity, 1f, true);
            this.CAPACITY = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, CachedImage> eldest) {
            if (size() > CAPACITY) {
                eldest.getValue().delete = true;
                return true;
            }
            return false;
        }
    }

    public static class CachedImage {
        private final int textureId;
        private final int width;
        private final int height;
        private final boolean dynamic;
        private boolean delete = false;

        private CachedImage(int textureId, int width, int height, boolean dynamic) {
            this.textureId = textureId;
            this.width = width;
            this.height = height;
            this.dynamic = dynamic;
        }

        public int getTextureId() {
            return textureId;
        }

        public void restore() {
            delete = false;
        }

        public void delete() {
            delete = true;
        }

        public boolean isDynamic() {
            return dynamic;
        }

        public boolean isPendingDeletion() {
            return delete;
        }
    }
}
