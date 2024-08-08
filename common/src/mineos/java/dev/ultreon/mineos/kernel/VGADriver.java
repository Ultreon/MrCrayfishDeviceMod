package dev.ultreon.mineos.kernel;

import dev.ultreon.devices.api.bios.*;
import dev.ultreon.devices.api.bios.efi.VEFI_DeviceID;
import dev.ultreon.devices.api.bios.efi.VEFI_DeviceInfo;
import dev.ultreon.devices.api.bios.efi.VEFI_System;

public class VGADriver extends VideoDriver {
    private int width;
    private int height;
    private int bitsPerPixel;
    private FrameBuffer fb;

    public VGADriver() {

    }

    @Override
    public void init(VEFI_System system, VEFI_DeviceID deviceID, VEFI_DeviceInfo deviceInfo) {
        Bios bios = system.getBios();
        FrameBufferInfo call = (FrameBufferInfo) bios.call(BiosCallType.FRAMEBUFFER_CALL, new Object[]{FrameBufferCall.GET_INFO, new Object[0]});

        bios.registerInterrupt(BiosInterruptType.FRAMEBUFFER_INTERRUPT, this::interrupt);

        this.width = call.width();
        this.height = call.height();
        this.bitsPerPixel = call.bpp();

        this.fb.init(deviceID, width, height, bitsPerPixel);
    }

    private void interrupt(InterruptData interruptData) {
        if ((int) interruptData.getField("type") == 0) {
            this.fb.resize(interruptData.getField("width"), interruptData.getField("height"));
        }
    }

    @Override
    public void load() {
        this.fb = new FrameBuffer();
    }

    @Override
    public void unload() {

    }

    @Override
    public String name() {
        return "";
    }
}
