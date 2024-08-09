package dev.ultreon.devices.impl.bios;

import dev.ultreon.vbios.FrameBufferCall;
import org.joml.Vector2i;

public class VirtVGA {
    private final Vector2i cachedSize = new Vector2i();
    private FrameBuffer frameBuffer;

    public VirtVGA(DisplayDevice device) {
        this.frameBuffer = new FrameBuffer(800, 600);
    }

    public Object call(FrameBufferCall callId, Object... data) {
        return this.frameBuffer.call(callId, data);
    }

    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }

    public void turnOff() {
        this.cachedSize.set(this.frameBuffer.width, this.frameBuffer.height);
        this.frameBuffer.dispose();
        this.frameBuffer = null;
    }

    public void reset() {
        this.frameBuffer.dispose();
        this.frameBuffer = new FrameBuffer(this.cachedSize.x, this.cachedSize.y);
    }
}
