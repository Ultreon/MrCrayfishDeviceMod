package dev.ultreon.devices.api.bios;

import java.nio.ByteBuffer;

public enum FrameBufferCall {
    GET_INFO(FrameBufferInfo.class), // -> FrameBufferInfo
    READ_PIXELS(void.class, ByteBuffer.class, int.class, int.class, int.class, int.class), // buffer, x, y, width, height -> void
    READ_PIXEL(int.class, short.class, short.class), // x, y -> color
    WRITE_PIXEL(void.class, short.class, short.class, int.class), // x, y, color -> void

    FILL_RECT(void.class, int.class, int.class, int.class, int.class, int.class), // x, y, width, height, color -> void
    DRAW_RECT(void.class, int.class, int.class, int.class, int.class, int.class), // x, y, width, height, color -> void
    DRAW_TEXT(void.class, int.class, int.class, String.class, int.class), // x, y, text, color -> void
    DRAW_ITEM(void.class, String.class, int.class, int.class, int.class), // registry-name, count, x, y -> void
    DRAW_IMAGE(void.class, String.class, int.class, int.class, int.class, int.class), // registry-name, x, y, width, height -> void

    CLEAR(void.class),
    BLIT(void.class, String.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class, int.class), // id, x, y, width, height, src-x, src-y, src-width, src-height, tex-width, tex-height -> void
    ;

    private final Class<?> returnType;
    private final Class<?>[] parameters;

    FrameBufferCall(Class<?> returnType, Class<?>... parameters) {
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Class<?>[] getParameters() {
        return parameters;
    }
}
