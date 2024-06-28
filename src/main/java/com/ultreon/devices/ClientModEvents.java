package com.ultreon.devices;

import com.ultreon.devices.block.entity.renderer.*;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.NativeImage;
import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.core.Laptop;
import com.ultreon.devices.init.DeviceBlockEntities;
import com.ultreon.devices.init.DeviceBlocks;
import com.ultreon.devices.object.AppInfo;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
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

    public static class ReloaderListener implements IFutureReloadListener {
        @NotNull
        @Override
        public CompletableFuture<Void> reload(@NotNull IStage preparationBarrier, @NotNull IResourceManager resourceManager, @NotNull IProfiler preparationsProfiler, @NotNull IProfiler reloadProfiler, @NotNull Executor backgroundExecutor, @NotNull Executor gameExecutor) {
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
            LOGGER.debug(SETUP, "Setting render layer for laptop {}", ForgeRegistries.BLOCKS.getKey(block));
            RenderTypeLookup.setRenderLayer(block, RenderType.cutout());
        });

        DeviceBlocks.getAllPrinters().forEach(block -> {
            LOGGER.debug(SETUP, "Setting render layer for printer {}", ForgeRegistries.BLOCKS.getKey(block));
            RenderTypeLookup.setRenderLayer(block, RenderType.cutout());
        });

        DeviceBlocks.getAllRouters().forEach(block -> {
            LOGGER.debug(SETUP, "Setting render layer for router {}", ForgeRegistries.BLOCKS.getKey(block));
            RenderTypeLookup.setRenderLayer(block, RenderType.cutout());
        });

        LOGGER.debug(SETUP, "Setting render layer for paper {}", ForgeRegistries.BLOCKS.getKey(DeviceBlocks.PAPER.get()));
        RenderTypeLookup.setRenderLayer(DeviceBlocks.PAPER.get(), RenderType.cutout());
    }

    private static void generateIconAtlas() {
        final int ICON_SIZE = 14;
        MyObject imageWriter = new MyObject(ICON_SIZE);

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

    private static void updateIcon(AppInfo info, int iconU, int iconV) {
        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconU, "iconU");
        ObfuscationReflectionHelper.setPrivateValue(AppInfo.class, info, iconV, "iconV");
    }

    public static void setRenderLayer(Block block, RenderType type) {
        RenderTypeLookup.setRenderLayer(block, type);
    }

    public static void registerRenderers() {
        LOGGER.info("Registering renderers.");

        ClientRegistry.bindTileEntityRenderer(DeviceBlockEntities.LAPTOP.get(), LaptopRenderer::new);
        ClientRegistry.bindTileEntityRenderer(DeviceBlockEntities.PRINTER.get(), PrinterRenderer::new);
        ClientRegistry.bindTileEntityRenderer(DeviceBlockEntities.PAPER.get(), PaperRenderer::new);
        ClientRegistry.bindTileEntityRenderer(DeviceBlockEntities.ROUTER.get(), RouterRenderer::new);
        ClientRegistry.bindTileEntityRenderer(DeviceBlockEntities.SEAT.get(), OfficeChairRenderer::new);
    }

    public static void registerLayerDefinitions() {
        LOGGER.info("Registering layer definitions.");
//        EntityModelLayerRegistry.register(PrinterRenderer.PaperModel.LAYER_LOCATION, PrinterRenderer.PaperModel::createBodyLayer);
    }

    private static class MyObject {
        final BufferedImage atlas;
        final Graphics g;
        private final int ICON_SIZE;
        int index;
        int mode;

        public MyObject(int ICON_SIZE) {
            this.ICON_SIZE = ICON_SIZE;
            atlas = new BufferedImage(ICON_SIZE * 16, ICON_SIZE * 16, BufferedImage.TYPE_INT_ARGB);
            g = atlas.createGraphics();
            index = 0;
            mode = 0;
        }

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
    }
}
