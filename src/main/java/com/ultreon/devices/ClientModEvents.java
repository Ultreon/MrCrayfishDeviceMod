package com.ultreon.devices;

import net.minecraft.client.renderer.texture.NativeImage;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.block.entity.renderer.LaptopRenderer;
import com.ultreon.devices.block.entity.renderer.OfficeChairRenderer;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.init.DeviceBlockEntities;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.object.AppInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


public class ClientModEvents {
    private static final Marker SETUP = MarkerFactory.getMarker("SETUP");
    private static final Logger LOGGER = Devices.LOGGER;

    public static void clientSetup() {
        LOGGER.info("Doing some client setup.");

        if (Devices.DEVELOPER_MODE) {
            LOGGER.info(SETUP, "Adding developer wallpaper.");
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/developer_wallpaper.png"));
        } else {
            LOGGER.info(SETUP, "Adding default wallpapers.");
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_1.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_2.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_3.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_4.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_5.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_6.png"));
            Laptop.addWallpaper(new ResourceLocation("devices:textures/gui/laptop_wallpaper_7.png"));
        }


        // Register other stuff.
        registerRenderLayers();
        registerRenderers();
        registerLayerDefinitions();
        generateIconAtlas();
    }

    public static class ReloaderListener implements PreparableReloadListener {
        @NotNull
        @Override
        public CompletableFuture<Void> reload(@NotNull PreparableReloadListener.PreparationBarrier preparationBarrier, @NotNull IResourceManager resourceManager, @NotNull ProfilerFiller preparationsProfiler, @NotNull ProfilerFiller reloadProfiler, @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
            LOGGER.debug("Reloading resources from the Device Mod.");

            return CompletableFuture.runAsync(() -> {
                if (ApplicationManager.getAllApplications().size() > 0) {
                    ApplicationManager.getAllApplications().forEach(AppInfo::reload);
                    generateIconAtlas();
                }
            }, gameExecutor);
        }
    }

    private static void registerRenderLayers() {
        if (true) return;
        DeviceBlocks.getAllLaptops().forEach(block -> {
            LOGGER.debug(SETUP, "Setting render layer for laptop {}", Registries.getId(block, Registry.BLOCK_REGISTRY));
            RenderTypeRegistry.register(RenderType.cutout(), block);
        });

        DeviceBlocks.getAllPrinters().forEach(block -> {
            LOGGER.debug(SETUP, "Setting render layer for printer {}", Registries.getId(block, Registry.BLOCK_REGISTRY));
            RenderTypeRegistry.register(RenderType.cutout(), block);
        });

        DeviceBlocks.getAllRouters().forEach(block -> {
            LOGGER.debug(SETUP, "Setting render layer for router {}", Registries.getId(block, Registry.BLOCK_REGISTRY));
            RenderTypeRegistry.register(RenderType.cutout(), block);
        });

        LOGGER.debug(SETUP, "Setting render layer for paper {}", Registries.getId(DeviceBlocks.PAPER.get(), Registry.BLOCK_REGISTRY));
        RenderTypeRegistry.register(RenderType.cutout(), DeviceBlocks.PAPER.get());
    }

    private static void generateIconAtlas() {
        final int ICON_SIZE = 14;
        var imageWriter = new Object() {
            final BufferedImage atlas = new BufferedImage(ICON_SIZE * 16, ICON_SIZE * 16, BufferedImage.TYPE_INT_ARGB);
            final Graphics g = atlas.createGraphics();
            int index = 0;
            int mode = 0;

            public boolean writeImage(AppInfo info, ResourceLocation location) {
                String path = "/assets/" + location.getNamespace() + "/" + location.getPath();
                try {
                    InputStream input = Devices.class.getResourceAsStream(path);
                    if (input != null) {
                        BufferedImage icon = ImageIO.read(input);
                        if (icon.getWidth() != ICON_SIZE || icon.getHeight() != ICON_SIZE) {
                            Devices.LOGGER.error("Incorrect icon size for " + (info == null ? null : info.getId()) + " (Must be 14 by 14 pixels)");
                            return false;
                        }
                        int iconU = (index % 16) * ICON_SIZE;
                        int iconV = (index / 16) * ICON_SIZE;
                        g.drawImage(icon, iconU, iconV, ICON_SIZE, ICON_SIZE, null);
                        if (info != null) {
                            AppInfo.Icon.Glyph glyph;
                            switch (mode) {
                                case 0:
                                    glyph = info.getIcon().getBase();
                                    break;
                                case 1:
                                    glyph = info.getIcon().getOverlay0();
                                    break;
                                case 2:
                                    glyph = info.getIcon().getOverlay1();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + mode);
                            }
                            glyph.setU(iconU);
                            glyph.setV(iconV);
                        }
                        index++;
                    } else {
                        Devices.LOGGER.error("Icon for application '" + (info == null ? null : info.getId()) + "' could not be found at '" + path + "'");
                    }
                } catch (Exception e) {
                    Devices.LOGGER.error("Unable to load icon for " + (info == null ? null : info.getId()));
                }
                return false;
            }

            public void finish() {
                g.dispose();
                try {
                    ImageIO.write(atlas, "png", Paths.get("it.png").toFile());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                 //   System.exit(-1);
                }

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    ImageIO.write(atlas, "png", output);
                    byte[] bytes = output.toByteArray();
                    ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                    Minecraft.getInstance().submit(() -> {
                        try {
                            Minecraft.getInstance().getTextureManager().register(Laptop.ICON_TEXTURES, new DynamicTexture(NativeImage.read(input)));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        imageWriter.writeImage(null, new ResourceLocation("devices", "textures/app/icon/base/missing.png"));


        for (AppInfo info : ApplicationManager.getAllApplications()) {
            if (info.getIcon() == null) continue;

            //ResourceLocation identifier = info.getId();
            //ResourceLocation iconResource = new ResourceLocation(info.getIcon());
            imageWriter.mode = 0;
            imageWriter.writeImage(info, info.getIcon().getBase().getResourceLocation());
            imageWriter.mode = 1;
            imageWriter.writeImage(info, info.getIcon().getOverlay0().getResourceLocation());
            imageWriter.mode = 2;
            imageWriter.writeImage(info, info.getIcon().getOverlay1().getResourceLocation());
        }
        imageWriter.mode = 0;
        imageWriter.finish();
    }

//    @ExpectPlatform
//    private static void updateIcon(AppInfo info, int iconU, int iconV) {
//        throw new AssertionError();
////        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconU, "iconU");
////        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconV, "iconV");
//    }

    @ExpectPlatform
    public static void setRenderLayer(Block block, RenderType type) {
        throw new AssertionError();
    }

    public static void registerRenderers() {
        LOGGER.info("Registering renderers.");

        TileEntityRendererRegistry.register(DeviceBlockEntities.LAPTOP.get(), LaptopRenderer::new);
        TileEntityRendererRegistry.register(DeviceBlockEntities.PRINTER.get(), PrinterRenderer::new);
        TileEntityRendererRegistry.register(DeviceBlockEntities.PAPER.get(), PaperRenderer::new);
        TileEntityRendererRegistry.register(DeviceBlockEntities.ROUTER.get(), RouterRenderer::new);
        TileEntityRendererRegistry.register(DeviceBlockEntities.SEAT.get(), OfficeChairRenderer::new);
    }

    public static void registerLayerDefinitions() {
        LOGGER.info("Registering layer definitions.");
//        EntityModelLayerRegistry.register(PrinterRenderer.PaperModel.LAYER_LOCATION, PrinterRenderer.PaperModel::createBodyLayer);
    }
}
