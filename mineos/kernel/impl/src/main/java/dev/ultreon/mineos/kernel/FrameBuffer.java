package dev.ultreon.mineos.kernel;

import dev.ultreon.devices.api.bios.BiosCallType;
import dev.ultreon.devices.api.bios.FrameBufferCall;
import dev.ultreon.devices.api.bios.efi.VEFI_DeviceID;
import dev.ultreon.devices.api.bios.efi.VEFI_System;

import java.util.UUID;

public class FrameBuffer {
    private VEFI_DeviceID deviceID = null;
    private VEFI_System system = null;
    private int width = 0;
    private int height = 0;
    private int bitsPerPixel = 0;

    public void init(VEFI_DeviceID deviceID, VEFI_System system, int width, int height, int bitsPerPixel) {
        this.deviceID = deviceID;
        this.system = system;
        this.width = width;
        this.height = height;
        this.bitsPerPixel = bitsPerPixel;
    }

    public int readPixel(int x, int y) {
        return (int) system.getBios().call(BiosCallType.FRAMEBUFFER_CALL, new Object[]{FrameBufferCall.READ_PIXEL, new Object[]{x, y}});
    }

    public void writePixel(int x, int y, int color) {
        system.getBios().call(BiosCallType.FRAMEBUFFER_CALL, new Object[]{FrameBufferCall.WRITE_PIXEL, new Object[]{x, y, color}});
    }

    public void clear(int color) {
        system.getBios().call(BiosCallType.FRAMEBUFFER_CALL, new Object[]{FrameBufferCall.CLEAR, new Object[]{color}});
    }

    public void fill(int x1, int y1, int x2, int y2, int color) {
        system.getBios().call(BiosCallType.FRAMEBUFFER_CALL, new Object[]{FrameBufferCall.FILL_RECT, new Object[]{x1, y1, x2, y2, color}});
    }

    public void renderOutline(int x1, int y1, int x2, int y2, int color) {
        system.getBios().call(BiosCallType.FRAMEBUFFER_CALL, new Object[]{FrameBufferCall.DRAW_RECT, new Object[]{x1, y1, x2, y2, color}});
    }

    public void blit(String id, int x, int y, int width, int height) {
        this.blit(id, x, y, width, height, 0, 0, width, height, 256, 256);
    }

    public void blit(String id, int x, int y, int width, int height, int srcX, int srcY) {
        this.blit(id, x, y, width, height, srcX, srcY, width, height, 256, 256);
    }

    public void blit(String id, int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight) {
        this.blit(id, x, y, width, height, srcX, srcY, srcWidth, srcHeight, 256, 256);
    }

    public void blit(String id, int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, int texWidth, int texHeight) {
        system.getBios().call(BiosCallType.FRAMEBUFFER_CALL, new Object[]{FrameBufferCall.BLIT, new Object[]{id, x, y, width, height, srcX, srcY, srcWidth, srcHeight, texWidth, texHeight}});
    }

    public void renderItemStack(String id, int count, int x, int y) {
        system.getBios().call(BiosCallType.FRAMEBUFFER_CALL, new Object[]{FrameBufferCall.DRAW_ITEM, new Object[]{id, count, x, y}});
    }

    public UUID getDeviceID() {
        return deviceID.id();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
