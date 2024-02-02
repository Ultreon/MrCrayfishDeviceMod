package com.ultreon.devices.cef;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.GLBuffers;
import com.ultreon.devices.Devices;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.ultreon.devices.cef.BrowserFramework.dimension;
import static org.lwjgl.opengl.GL11C.*;

public class BrowserGraphics implements GLEventListener {
    public static final ResourceLocation RES = Devices.id("dynamic/app/browser");
    private final java.awt.Component uiComponent;

    private static final Object bufferLock = new Object();
    static ByteBuffer buffer;
    private int bufferWidth;
    private int bufferHeight;
    private boolean init;

    public BrowserGraphics(GLCanvas uiComponent) {
        this.uiComponent = uiComponent;
    }

    public static void locked(Runnable r) {
        synchronized (bufferLock) {
            try {
                r.run();
            } catch (Exception e) {
                Devices.criticalCrash(e);
            } finally {
                bufferLock.notifyAll();
            }
        }
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        synchronized (bufferLock) {
            buffer = GLBuffers.newDirectByteBuffer(uiComponent.getWidth() * uiComponent.getHeight() * 4);
            bufferWidth = uiComponent.getWidth();
            bufferHeight = uiComponent.getHeight();
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        synchronized (bufferLock) {
            buffer.clear();
            buffer = null;
            bufferWidth = 0;
            bufferHeight = 0;
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        saveImage((GL3) drawable.getGL(), uiComponent.getWidth(), uiComponent.getHeight());
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        synchronized (bufferLock) {
            buffer.clear();
            buffer = GLBuffers.newDirectByteBuffer(width * height * 4);
            dimension.width = width;
            dimension.height = height;
        }

    }

    protected void saveImage(GL3 gl3, int width, int height) {

        synchronized (bufferLock) {
            if (buffer == null) return;

            if (bufferWidth != width || bufferHeight != height) {
                buffer.clear();
                buffer = GLBuffers.newDirectByteBuffer(width * height * 4);
                bufferWidth = width;
                bufferHeight = height;
            }

            // be sure you are reading from the right fbo (here is supposed to be the default one)
            // bind the right buffer to read from
            gl3.glReadBuffer(GL_BACK);
            // if the width is not multiple of 4, set unpackPixel = 1
            gl3.glPixelStorei(GL_UNPACK_ALIGNMENT, width % 4 == 0 ? 0 : 1);
            buffer.position(0);

            // read pixels
            gl3.glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

            Devices.LOGGER.info("Buffer size: " + buffer.capacity());
            Devices.LOGGER.info("Display size: " + width + "x" + height);

            int position = buffer.position();
            if (position < buffer.capacity()) {
                Devices.LOGGER.error("Buffer underflow! " + position + " < " + buffer.capacity());
                IntBuffer intBuffer = buffer.asIntBuffer();
                int[] ints = new int[intBuffer.capacity()];
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                for (int i = 0; i < ints.length; i++) {
                    ints[i] = intBuffer.get();
                    image.setRGB(i % width, height - i / width - 1, ints[i]);
                }

                BMPFile bmpFile = new BMPFile();
                bmpFile.saveBitmap("buffer.bmp", ints, width, height);

                try {
                    ImageIO.write(image, "png", Path.of("buffer.png").toFile());
                } catch (IOException e) {
                    Devices.LOGGER.error("Failed to dump display buffer as png", e);
                }

                buffer.position(0);

                try (FileOutputStream f = new FileOutputStream(Path.of("buffer.bin").toFile())) {
                    for (int i = 0; i < buffer.capacity(); i++) {
                        f.write(buffer.get(i));
                    }
                } catch (IOException e) {
                    Devices.LOGGER.error("Failed to dump display buffer", e);
                }
            }
        }
    }
}
