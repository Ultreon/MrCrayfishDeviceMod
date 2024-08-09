package dev.ultreon.vbios.efi;

import dev.ultreon.devices.api.TextureLoader;
import dev.ultreon.vbios.Bios;
import dev.ultreon.devices.api.device.VEFI_Disk;
import org.intellij.lang.annotations.Language;

import java.io.IOException;

public interface VEFI_System extends TextureLoader {
    int F_READ = 0;
    int F_WRITE = 1;
    int F_EXEC = 2;

    VEFI_DeviceInfo getDevices();

    VEFI_DiskInfo[] getDriveList() throws IOException;

    void sendNotification(VEFI_Notification notification);

    void writePortB(VEFI_DeviceID id, int index, byte value) throws IOException;
    void writePortS(VEFI_DeviceID id, int index, short value) throws IOException;
    void writePortI(VEFI_DeviceID id, int index, int value) throws IOException;
    void writePortL(VEFI_DeviceID id, int index, long value) throws IOException;
    void writePortF(VEFI_DeviceID id, int index, float value) throws IOException;
    void writePortD(VEFI_DeviceID id, int index, double value) throws IOException;
    void writePortC(VEFI_DeviceID id, int index, char value) throws IOException;
    void writePortZ(VEFI_DeviceID id, int index, boolean value) throws IOException;
    void writePortT(VEFI_DeviceID id, int index, String value) throws IOException;

    void writePortBv(VEFI_DeviceID id, int index, byte[] value) throws IOException;
    void writePortSv(VEFI_DeviceID id, int index, short[] value) throws IOException;
    void writePortIv(VEFI_DeviceID id, int index, int[] value) throws IOException;
    void writePortLv(VEFI_DeviceID id, int index, long[] value) throws IOException;
    void writePortFv(VEFI_DeviceID id, int index, float[] value) throws IOException;
    void writePortDv(VEFI_DeviceID id, int index, double[] value) throws IOException;
    void writePortCv(VEFI_DeviceID id, int index, char[] value) throws IOException;
    void writePortZv(VEFI_DeviceID id, int index, boolean[] value) throws IOException;
    void writePortTv(VEFI_DeviceID id, int index, String[] value) throws IOException;

    byte readPortB(VEFI_DeviceID id, int index) throws IOException;
    short readPortS(VEFI_DeviceID id, int index) throws IOException;
    int readPortI(VEFI_DeviceID id, int index) throws IOException;
    long readPortL(VEFI_DeviceID id, int index) throws IOException;
    float readPortF(VEFI_DeviceID id, int index) throws IOException;
    double readPortD(VEFI_DeviceID id, int index) throws IOException;
    char readPortC(VEFI_DeviceID id, int index) throws IOException;
    boolean readPortZ(VEFI_DeviceID id, int index) throws IOException;
    String readPortT(VEFI_DeviceID id, int index) throws IOException;

    byte[] readPortBv(VEFI_DeviceID id, int index, int length) throws IOException;
    short[] readPortSv(VEFI_DeviceID id, int index, int length) throws IOException;
    int[] readPortIv(VEFI_DeviceID id, int index, int length) throws IOException;
    long[] readPortLv(VEFI_DeviceID id, int index, int length) throws IOException;
    float[] readPortFv(VEFI_DeviceID id, int index, int length) throws IOException;
    double[] readPortDv(VEFI_DeviceID id, int index, int length) throws IOException;
    char[] readPortCv(VEFI_DeviceID id, int index, int length) throws IOException;
    boolean[] readPortZv(VEFI_DeviceID id, int index, int length) throws IOException;
    String[] readPortTv(VEFI_DeviceID id, int index, int length) throws IOException;

    void writePortB(VEFI_DeviceID id, int index, byte value, int mask) throws IOException;
    void writePortS(VEFI_DeviceID id, int index, short value, int mask) throws IOException;
    void writePortI(VEFI_DeviceID id, int index, int value, int mask) throws IOException;
    void writePortL(VEFI_DeviceID id, int index, long value, int mask) throws IOException;
    void writePortF(VEFI_DeviceID id, int index, float value, int mask) throws IOException;
    void writePortD(VEFI_DeviceID id, int index, double value, int mask) throws IOException;

    VEFI_Disk openDisk(VEFI_DiskInfo info);

    VEFI_File openFile(VEFI_Disk info, String path, int mode);

    void writeFileB(VEFI_File file, byte[] data, int offset, int length);
    void writeFileT(VEFI_File file, String data, int offset, int length);

    void readFileB(VEFI_File file, byte[] data, int offset, int length);
    void readFileT(VEFI_File file, char[] data, int offset, int length);

    /**
     * Closes a file handle
     *
     * @param file the file to close
     */
    void closeFile(VEFI_File file);

    /**
     * Get the vBIOS instance.
     *
     * @return the vBIOS
     */
    Bios getBios();

    /**
     * Run a class in an isolated context.
     *
     * @param context the context class, this is used to find the location in the classpath
     * @param className the class name of the class to run in the context
     * @param packageName the allowed package name that can be loaded.
     * @param args the arguments to pass to the class.
     * @return the instance of the class in the isolated context
     */
    Object runIsolated(Class<?> context, @Language("jvm-class-name") String className, String packageName, Object... args);

    /**
     * Closes a disk after it is no longer in use.
     *
     * @param vefiDisk the vEFI disk to close
     * @throws IOException if an I/O error occurs or if the disk is already closed
     */
    void closeDisk(VEFI_Disk vefiDisk) throws IOException;

    /**
     * Offloads code to the vGPU thread.
     * This is basically the Minecraft render thread.
     *
     * @param call the code to offload
     */
    void offload(Runnable call);
}
