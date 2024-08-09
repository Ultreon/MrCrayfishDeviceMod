package dev.ultreon.devices.api;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public interface IO {
    IO NULL = new IO() {
        @Override
        public int get(int index) {
            return -1;
        }

        @Override
        public boolean set(int index, int value) {
            return false;
        }

        @Override
        public long size() {
            return 0;
        }
    };

    default int read(byte[] buffer, int address, int length) throws EOFException {
        for (int i = 0; i < length; i++) {
            int aByte = get(address + i);
            if (aByte == -1) throw new EOFException("End of device reached");
            buffer[i] = (byte) aByte;
        }
        return length;
    }

    default void write(byte[] buffer, int address, int length) throws EOFException {
        for (int i = 0; i < length; i++) {
            if (!set(address + i, buffer[i])) {
                throw new EOFException("End of device reached");
            }
        }
    }

    int get(int index);

    default byte getByte(int index) throws IOException {
        if (index < 0) {
            throw new IOException("Index out of bounds");
        }

        int b = get(index);
        if (b == -1) {
            throw new EOFException("End of device reached");
        }
        return (byte) b;
    }
    default short getShort(int index) throws IOException {
        byte b1 = getByte(index);
        byte b2 = getByte(index + 1);
        return (short) ((b1 << 8) | (b2 & 0xFF));
    }
    default int getInt(int index) throws IOException {
        byte b1 = getByte(index);
        byte b2 = getByte(index + 1);
        byte b3 = getByte(index + 2);
        byte b4 = getByte(index + 3);
        return (b1 << 24) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 8) | (b4 & 0xFF);
    }
    default long getLong(int index) throws IOException {
        byte b1 = getByte(index);
        byte b2 = getByte(index + 1);
        byte b3 = getByte(index + 2);
        byte b4 = getByte(index + 3);
        byte b5 = getByte(index + 4);
        byte b6 = getByte(index + 5);
        byte b7 = getByte(index + 6);
        byte b8 = getByte(index + 7);
        return ((long) b1 << 56) | ((b2 & 0xFFL) << 48) | ((b3 & 0xFFL) << 40) | ((b4 & 0xFFL) << 32) | ((b5 & 0xFFL) << 24) | ((b6 & 0xFFL) << 16) | ((b7 & 0xFFL) << 8) | (b8 & 0xFFL);
    }
    default float getFloat(int index) throws IOException {
        byte b1 = getByte(index);
        byte b2 = getByte(index + 1);
        byte b3 = getByte(index + 2);
        byte b4 = getByte(index + 3);
        return Float.intBitsToFloat((b1 << 24) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 8) | (b4 & 0xFF));
    }
    default double getDouble(int index) throws IOException {
        byte b1 = getByte(index);
        byte b2 = getByte(index + 1);
        byte b3 = getByte(index + 2);
        byte b4 = getByte(index + 3);
        byte b5 = getByte(index + 4);
        byte b6 = getByte(index + 5);
        byte b7 = getByte(index + 6);
        byte b8 = getByte(index + 7);

        long l = ((long) b1 << 56) | ((b2 & 0xFFL) << 48) | ((b3 & 0xFFL) << 40) | ((b4 & 0xFFL) << 32) | ((b5 & 0xFFL) << 24) | ((b6 & 0xFFL) << 16) | ((b7 & 0xFFL) << 8) | (b8 & 0xFFL);
        return Double.longBitsToDouble(l);
    }
    default char getChar(int index) throws IOException {
        byte b1 = getByte(index);
        byte b2 = getByte(index + 1);
        return (char) ((b1 << 8) | (b2 & 0xFF));
    }
    default String getString(int index) throws IOException {
        int length = getInt(index);
        byte[] bytes = getBytes(index + 4, length);
        return new String(bytes, StandardCharsets.UTF_8);
    }
    default byte[] getBytes(int index, int length) throws IOException {
        byte[] bytes = new byte[length];
        if (read(bytes, index, length) != length) {
            throw new EOFException("End of device reached");
        }
        return bytes;
    }

    boolean set(int index, int value);

    default void setByte(int index, byte value) throws IOException {
        if (!set(index, value)) {
            throw new EOFException("End of device reached");
        }
    }
    default void setShort(int index, short value) throws IOException {
        setByte(index, (byte) (value >> 8));
        setByte(index + 1, (byte) (value & 0xFF));
    }
    default void setInt(int index, int value) throws IOException {
        setByte(index, (byte) (value >> 24));
        setByte(index + 1, (byte) ((value >> 16) & 0xFF));
        setByte(index + 2, (byte) ((value >> 8) & 0xFF));
        setByte(index + 3, (byte) (value & 0xFF));
    }
    default void setLong(int index, long value) throws IOException {
        setByte(index, (byte) (value >> 56));
        setByte(index + 1, (byte) ((value >> 48) & 0xFF));
        setByte(index + 2, (byte) ((value >> 40) & 0xFF));
        setByte(index + 3, (byte) ((value >> 32) & 0xFF));
        setByte(index + 4, (byte) ((value >> 24) & 0xFF));
        setByte(index + 5, (byte) ((value >> 16) & 0xFF));
        setByte(index + 6, (byte) ((value >> 8) & 0xFF));
        setByte(index + 7, (byte) (value & 0xFF));
    }
    default void setFloat(int index, float value) throws IOException {
        int i = Float.floatToIntBits(value);
        setByte(index, (byte) (i >> 24));
        setByte(index + 1, (byte) ((i >> 16) & 0xFF));
        setByte(index + 2, (byte) ((i >> 8) & 0xFF));
        setByte(index + 3, (byte) (i & 0xFF));
    }
    default void setDouble(int index, double value) throws IOException {
        long l = Double.doubleToLongBits(value);
        setByte(index, (byte) (l >> 56));
        setByte(index + 1, (byte) ((l >> 48) & 0xFF));
        setByte(index + 2, (byte) ((l >> 40) & 0xFF));
        setByte(index + 3, (byte) ((l >> 32) & 0xFF));
        setByte(index + 4, (byte) ((l >> 24) & 0xFF));
        setByte(index + 5, (byte) ((l >> 16) & 0xFF));
        setByte(index + 6, (byte) ((l >> 8) & 0xFF));
        setByte(index + 7, (byte) (l & 0xFF));
    }
    default void setChar(int index, char value) throws IOException {
        setByte(index, (byte) (value >> 8));
        setByte(index + 1, (byte) (value & 0xFF));
    }
    default void setString(int index, String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        setInt(index, bytes.length);
        setBytes(index + 4, bytes);
    }
    default void setBytes(int index, byte[] value) throws IOException {
        for (int i = 0; i < value.length; i++) {
            setByte(index + i, value[i]);
        }
    }

    long size();
}
