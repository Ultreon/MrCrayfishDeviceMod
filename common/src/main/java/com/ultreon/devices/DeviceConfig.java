package com.ultreon.devices;

import dev.architectury.platform.Platform;
import io.github.xypercode.craftyconfig.ConfigEntry;
import io.github.xypercode.craftyconfig.ConfigInfo;
import io.github.xypercode.craftyconfig.CraftyConfig;
import io.github.xypercode.craftyconfig.Ranged;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

@ConfigInfo(fileName = "devices")
public class DeviceConfig extends CraftyConfig {
    @ConfigEntry(path = "laptop.pingRate", comment = "The amount of ticks the laptop waits until sending another ping to it's connected router.")
    @Ranged(min = 1, max = 200)
    public static int PING_RATE = 20;

    @ConfigEntry(path = "router.signalRange", comment = "The range of the router's signal.")
    @Ranged(min = 10, max = 100)
    public static int SIGNAL_RANGE = 20;
    @ConfigEntry(path = "router.beaconInterval", comment = "The amount of ticks between router beacons.")
    @Ranged(min = 1, max = 200)
    public static int BEACON_INTERVAL = 20;
    @ConfigEntry(path = "router.maxDevices", comment = "The maximum amount of devices that can be connected to the router.")
    @Ranged(min = 1, max = 64)
    public static int MAX_DEVICES = 16;

    @ConfigEntry(path = "printer.overridePrintSpeed", comment = "If the printer should override the print speed.")
    public static boolean OVERRIDE_PRINT_SPEED = false;
    @ConfigEntry(path = "printer.customPrintSpeed", comment = "The custom print speed.")
    @Ranged(min = 1, max = 600)
    public static int CUSTOM_PRINT_SPEED = 20;
    @ConfigEntry(path = "printer.maxPaperCount", comment = "The maximum amount of paper that can be printed.")
    @Ranged(min = 1, max = 99)
    public static int MAX_PAPER_COUNT = 64;

    @ConfigEntry(path = "pixelPainter.enable", comment = "If the pixel painter should be enabled.")
    public static boolean PIXEL_PAINTER_ENABLE = true;
    @ConfigEntry(path = "pixelPainter.renderPrinted3D", comment = "If the pixel painter should render printed 3D.")
    public static boolean RENDER_PRINTED_3D = true;

    @ConfigEntry(path = "debug.debugButton", comment = "If the debug button should be enabled.")
    public static boolean DEBUG_BUTTON = Platform.isDevelopmentEnvironment();

    public static void readSyncTag(CompoundTag tag) {
        if (tag.contains("pingRate", Tag.TAG_INT)) PING_RATE = tag.getInt("pingRate");
        if (tag.contains("signalRange", Tag.TAG_INT)) SIGNAL_RANGE = tag.getInt("signalRange");
    }

    public static CompoundTag writeSyncTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("pingRate", PING_RATE);
        tag.putInt("signalRange", SIGNAL_RANGE);
        return tag;
    }
}
