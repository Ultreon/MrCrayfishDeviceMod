package dev.ultreon.devices.impl.bios;

import com.mojang.blaze3d.platform.NativeImage;
import de.waldheinz.fs.BlockDevice;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ultreon.vbios.Bios;
import dev.ultreon.devices.api.device.HardwareDevice;
import dev.ultreon.devices.api.device.VEFI_Disk;
import dev.ultreon.devices.core.client.ClientNotification;
import dev.ultreon.vbios.efi.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VEFI_SystemImpl implements VEFI_System {
    public static final byte EFI_SIGNATURE = (byte) 0x83;
    private final VEFI_DeviceID[] deviceInfo;
    private final Map<UUID, HardwareDevice> devices = new ConcurrentHashMap<>();
    private final VEFI_DeviceID[] driveInfo;
    private final VBios vBios;
    private final BitSet openFiles = new BitSet(128);
    private final VEFI_FileContext[] files = new VEFI_FileContext[128];
    private final BitSet openDisks = new BitSet(32);
    final VEFI_DiskHandle[] disks = new VEFI_DiskHandle[32];
    private final Int2ObjectMap<AbstractTexture> textures = new Int2ObjectArrayMap<>();

    public VEFI_SystemImpl(DeviceInfo deviceInfo, VBios vBios) {
        this.deviceInfo = deviceInfo.createDeviceList();
        this.driveInfo = deviceInfo.createDriveList();

        for (VEFI_DeviceID id : deviceInfo.deviceList) {
            this.devices.put(id.id(), vBios.getPhysicalDevice(id.id()));
        }
        this.vBios = vBios;
    }

    @Override
    public VEFI_DeviceInfo getDevices() {
        VEFI_DeviceID[] devices = new VEFI_DeviceID[deviceInfo.length];
        System.arraycopy(deviceInfo, 0, devices, 0, deviceInfo.length);
        return new VEFI_DeviceInfo(devices);
    }

    @Override
    public VEFI_DiskInfo[] getDriveList() throws IOException {
        VEFI_DiskInfo[] drives = new VEFI_DiskInfo[driveInfo.length];
        for (int i = 0; i < driveInfo.length; i++) {
            drives[i] = new VEFI_DiskInfo(driveInfo[i], readPortB(driveInfo[i], 0));
        }
        return drives;
    }

    @Override
    public void sendNotification(VEFI_Notification notification) {
        try {
            ClientNotification clientNotification = ClientNotification.of(notification);
            clientNotification.push();
        } catch (Throwable fault) {
            this.vBios.fault(fault);
        }
    }

    @Override
    public void writePortB(VEFI_DeviceID id, int index, byte value) throws IOException {
        this.devices.get(id.id()).io().setByte(index, value);
    }

    @Override
    public void writePortS(VEFI_DeviceID id, int index, short value) throws IOException {
        this.devices.get(id.id()).io().setShort(index, value);
    }

    @Override
    public void writePortI(VEFI_DeviceID id, int index, int value) throws IOException {
        this.devices.get(id.id()).io().setInt(index, value);
    }

    @Override
    public void writePortL(VEFI_DeviceID id, int index, long value) throws IOException {
        this.devices.get(id.id()).io().setLong(index, value);
    }

    @Override
    public void writePortF(VEFI_DeviceID id, int index, float value) throws IOException {
        this.devices.get(id.id()).io().setFloat(index, value);
    }

    @Override
    public void writePortD(VEFI_DeviceID id, int index, double value) throws IOException {
        this.devices.get(id.id()).io().setDouble(index, value);
    }

    @Override
    public void writePortC(VEFI_DeviceID id, int index, char value) throws IOException {
        this.devices.get(id.id()).io().setChar(index, value);
    }

    @Override
    public void writePortZ(VEFI_DeviceID id, int index, boolean value) throws IOException {
        this.devices.get(id.id()).io().setByte(index, (byte) (value ? 1 : 0));
    }

    @Override
    public void writePortT(VEFI_DeviceID id, int index, String value) throws IOException {
        this.devices.get(id.id()).io().setString(index, value);
    }

    @Override
    public void writePortBv(VEFI_DeviceID id, int index, byte[] value) throws IOException {
        this.devices.get(id.id()).io().setBytes(index, value);
    }

    @Override
    public void writePortSv(VEFI_DeviceID id, int index, short[] value) throws IOException {
        for (int i = 0; i < value.length; i++) {
            this.devices.get(id.id()).io().setShort(index + i * Short.BYTES, value[i]);
        }
    }

    @Override
    public void writePortIv(VEFI_DeviceID id, int index, int[] value) throws IOException {
        for (int i = 0; i < value.length; i++) {
            this.devices.get(id.id()).io().setInt(index + i * Integer.BYTES, value[i]);
        }
    }

    @Override
    public void writePortLv(VEFI_DeviceID id, int index, long[] value) throws IOException {
        for (int i = 0; i < value.length; i++) {
            this.devices.get(id.id()).io().setLong(index + i * Long.BYTES, value[i]);
        }
    }

    @Override
    public void writePortFv(VEFI_DeviceID id, int index, float[] value) throws IOException {
        for (int i = 0; i < value.length; i++) {
            this.devices.get(id.id()).io().setFloat(index + i * Float.BYTES, value[i]);
        }
    }

    @Override
    public void writePortDv(VEFI_DeviceID id, int index, double[] value) throws IOException {
        for (int i = 0; i < value.length; i++) {
            this.devices.get(id.id()).io().setDouble(index + i * Double.BYTES, value[i]);
        }
    }

    @Override
    public void writePortCv(VEFI_DeviceID id, int index, char[] value) throws IOException {
        for (int i = 0; i < value.length; i++) {
            this.devices.get(id.id()).io().setChar(index + i * Character.BYTES, value[i]);
        }
    }

    @Override
    public void writePortZv(VEFI_DeviceID id, int index, boolean[] value) throws IOException {
        for (int i = 0; i < value.length; i++) {
            this.devices.get(id.id()).io().setByte(index + i, (byte) (value[i] ? 1 : 0));
        }
    }

    @Override
    public void writePortTv(VEFI_DeviceID id, int index, String[] value) throws IOException {
        for (int i = 0; i < value.length; i++) {
            this.devices.get(id.id()).io().
                    setString(index + i * Character.BYTES, value[i]);
        }
    }

    @Override
    public byte readPortB(VEFI_DeviceID id, int index) throws IOException {
        return this.devices.get(id.id()).io().getByte(index);
    }

    @Override
    public short readPortS(VEFI_DeviceID id, int index) throws IOException {
        return this.devices.get(id.id()).io().getShort(index);
    }

    @Override
    public int readPortI(VEFI_DeviceID id, int index) throws IOException {
        return this.devices.get(id.id()).io().getInt(index);
    }

    @Override
    public long readPortL(VEFI_DeviceID id, int index) throws IOException {
        return this.devices.get(id.id()).io().getLong(index);
    }

    @Override
    public float readPortF(VEFI_DeviceID id, int index) throws IOException {
        return this.devices.get(id.id()).io().getFloat(index);
    }

    @Override
    public double readPortD(VEFI_DeviceID id, int index) throws IOException {
        return this.devices.get(id.id()).io().getDouble(index);
    }

    @Override
    public char readPortC(VEFI_DeviceID id, int index) throws IOException {
        return this.devices.get(id.id()).io().getChar(index);
    }

    @Override
    public boolean readPortZ(VEFI_DeviceID id, int index) throws IOException {
        return this.devices.get(id.id()).io().getByte(index) != 0;
    }

    @Override
    public String readPortT(VEFI_DeviceID id, int index) throws IOException {
        return this.devices.get(id.id()).io().getString(index);
    }

    @Override
    public byte[] readPortBv(VEFI_DeviceID id, int index, int length) throws IOException {
        return this.devices.get(id.id()).io().getBytes(index, length);
    }

    @Override
    public short[] readPortSv(VEFI_DeviceID id, int index, int length) throws IOException {
        short[] shorts = new short[length];
        for (int i = 0; i < length; i++) {
            shorts[i] = this.devices.get(id.id()).io().getShort(index + i * Short.BYTES);
        }
        return shorts;
    }

    @Override
    public int[] readPortIv(VEFI_DeviceID id, int index, int length) throws IOException {
        int[] ints = new int[length];
        for (int i = 0; i < length; i++) {
            ints[i] = this.devices.get(id.id()).io().getInt(index + i * Integer.BYTES);
        }
        return ints;
    }

    @Override
    public long[] readPortLv(VEFI_DeviceID id, int index, int length) throws IOException {
        long[] longs = new long[length];
        for (int i = 0; i < length; i++) {
            longs[i] = this.devices.get(id.id()).io().getLong(index + i * Long.BYTES);
        }
        return longs;
    }

    @Override
    public float[] readPortFv(VEFI_DeviceID id, int index, int length) throws IOException {
        float[] floats = new float[length];
        for (int i = 0; i < length; i++) {
            floats[i] = this.devices.get(id.id()).io().getFloat(index + i * Float.BYTES);
        }
        return floats;
    }

    @Override
    public double[] readPortDv(VEFI_DeviceID id, int index, int length) throws IOException {
        double[] doubles = new double[length];
        for (int i = 0; i < length; i++) {
            doubles[i] = this.devices.get(id.id()).io().getDouble(index + i * Double.BYTES);
        }
        return doubles;
    }

    @Override
    public char[] readPortCv(VEFI_DeviceID id, int index, int length) throws IOException {
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = this.devices.get(id.id()).io().getChar(index + i * Character.BYTES);
        }
        return chars;
    }

    @Override
    public boolean[] readPortZv(VEFI_DeviceID id, int index, int length) throws IOException {
        boolean[] booleans = new boolean[length];
        for (int i = 0; i < length; i++) {
            booleans[i] = this.devices.get(id.id()).io().getByte(index + i) != 0;
        }
        return booleans;
    }

    @Override
    public String[] readPortTv(VEFI_DeviceID id, int index, int length) throws IOException {
        String[] strings = new String[length];
        for (int i = 0; i < length; i++) {
            strings[i] = this.devices.get(id.id()).io().getString(index + i * Integer.BYTES);
        }
        return strings;
    }

    @Override
    public void writePortB(VEFI_DeviceID id, int index, byte value, int mask) throws IOException {
        this.devices.get(id.id()).io().setByte(index, (byte) (value & mask));
    }

    @Override
    public void writePortS(VEFI_DeviceID id, int index, short value, int mask) throws IOException {
        this.devices.get(id.id()).io().setShort(index, (short) (value & mask));
    }

    @Override
    public void writePortI(VEFI_DeviceID id, int index, int value, int mask) throws IOException {
        this.devices.get(id.id()).io().setInt(index, value & mask);
    }

    @Override
    public void writePortL(VEFI_DeviceID id, int index, long value, int mask) throws IOException {
        this.devices.get(id.id()).io().setLong(index, value & mask);
    }

    @Override
    public void writePortF(VEFI_DeviceID id, int index, float value, int mask) throws IOException {
        this.devices.get(id.id()).io().setFloat(index, Float.intBitsToFloat(Float.floatToIntBits(value) & mask));
    }

    @Override
    public void writePortD(VEFI_DeviceID id, int index, double value, int mask) throws IOException {
        this.devices.get(id.id()).io().setDouble(index, Double.longBitsToDouble(Double.doubleToLongBits(value) & mask));
    }

    @Override
    public VEFI_Disk openDisk(VEFI_DiskInfo info) {
        VEFI_DeviceID id = info.deviceID();
        if (!this.devices.containsKey(id.id()) || !id.isDrive()) {
            return null;
        }

        int i = this.openDisks.nextClearBit(0);

        this.openDisks.set(i);
        HardwareDevice hardwareDevice = this.devices.get(id.id());
        if (hardwareDevice instanceof BlockDevice blockDevice) {
            VEFI_DiskHandle handle = new VEFI_DiskHandle(this, hardwareDevice, blockDevice, i);
            this.disks[i] = handle;
            try {
                this.loadPartitions(handle);
            } catch (IOException e) {
                this.vBios.interrupt(new IOErrorInterrupt(this.vBios, this, e));
            }
        } else {
            this.disks[i] = null;
            this.openDisks.clear(i);
        }

        return new VEFI_Disk(i);
    }

    private void loadPartitions(VEFI_DiskHandle disk) throws IOException {
        PartitionTable partitionTable = PartitionTable.load(disk.blockDevice());
        partitionTable.entries.forEach((entry) -> {
            if (entry.type == EFI_SIGNATURE) {
                disk.findBootPartition(entry);
            }
        });
    }

    @Override
    public VEFI_File openFile(VEFI_Disk info, String path, int mode) {
        VEFI_DiskHandle disk = this.disks[info.handle()];
        int i = this.openFiles.nextClearBit(0);
        disk.openFiles.set(i);
        this.files[i] = new VEFI_FileContext(disk.hardwareDevice(), path, mode, i);

        return new VEFI_File(i);
    }

    @Override
    public void writeFileB(VEFI_File file, byte[] data, int offset, int length) {
        this.files[file.handle()].write(data, offset, length);
    }

    @Override
    public void writeFileT(VEFI_File file, String data, int offset, int length) {

    }

    @Override
    public void readFileB(VEFI_File file, byte[] data, int offset, int length) {

    }

    @Override
    public void readFileT(VEFI_File file, char[] data, int offset, int length) {

    }

    @Override
    public void closeFile(VEFI_File file) {

    }

    @Override
    public Bios getBios() {
        return vBios;
    }

    @Override
    public Object runIsolated(Class<?> context, String className, String packageName, Object... args) {
        IsolatedClassLoader classLoader = new IsolatedClassLoader(getClass().getClassLoader(), context.getProtectionDomain().getCodeSource().getLocation(), packageName);
        try {
            Class<?> theClass = classLoader.loadClass(className);
            Class<?>[] parameterTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
            return theClass.getConstructor(parameterTypes).newInstance(args);
        } catch (Throwable e) {
            vBios.fault(e);

            class BrokenObject {}
            return new BrokenObject();
        }
    }

    public VEFI_FileContext getFile(int handle) {
        return files[handle];
    }

    @Override
    public void closeDisk(VEFI_Disk vefiDisk) throws IOException {
        VEFI_DiskHandle handle = disks[vefiDisk.handle()];
        if (handle != null) {
            handle.close();
        }

        disks[vefiDisk.handle()] = null;
        openDisks.clear(vefiDisk.handle());
    }

    @Override
    public void offload(Runnable call) {
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> Minecraft.getInstance().submit(call));
    }

    @Override
    public int load(InputStream stream) throws IOException {
        NativeImage image = NativeImage.read(stream);
        AbstractTexture texture = new DynamicTexture(image);

        this.textures.put(texture.getId(), texture);

        return texture.getId();
    }

    @Override
    public void destroy(int textureID) {
        AbstractTexture texture = this.textures.remove(textureID);
        if (texture != null) {
            texture.releaseId();
            texture.close();
        }
    }
}
