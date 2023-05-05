package com.ultreon.devices.debug;

import dev.architectury.platform.Platform;

import java.io.*;

public class Debugger {
    public static final String MARKER_FILE_SYSTEM = "FileSystem";
    public static final String MARKER_APPLICATION = "Application";
    public static final String MARKER_SYSTEM = "System";
    public static final String MARKER_BLOCK_ENTITY = "LaptopBlockEntity";
    public static final String MARKER_BLOCK = "LaptopBlock";
    public static final String MARKER_FLASH_DRIVE = "FlashDrive";
    public static final String MARKER_PRINTER = "Printer";
    public static final String MARKER_PRINTER_RENDERER = "PrinterRenderer";
    public static final String MARKER_FILE = "File";
    public static final String MARKER_DIRECTORY = "Dir";
    public static final String MARKER_USAGE = "Usage";
    private static final PrintStream debugFile;

    static {
        File file = new File("logs/devices-mod.debug.log");
        if (file.exists() && !file.delete()) throw new RuntimeException("Failed to delete log file: " + file.getPath());

        try {
            if (!file.createNewFile()) throw new RuntimeException("Failed to create log file: " + file.getPath());

            debugFile = new PrintStream(new FileOutputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(debugFile::close));
    }

    public static void log(String marker, String message) {
        String msg = marker + " >> " + message;
        if (Platform.isDevelopmentEnvironment()) {
            System.out.println(msg);
        }
    }

    public static PrintStream getDebugFile() {
        return debugFile;
    }
}
