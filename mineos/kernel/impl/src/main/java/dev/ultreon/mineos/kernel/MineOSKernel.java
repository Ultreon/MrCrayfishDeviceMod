package dev.ultreon.mineos.kernel;

import dev.ultreon.devices.api.bios.Bios;
import dev.ultreon.devices.api.bios.BiosCallType;
import dev.ultreon.devices.api.bios.FrameBufferCall;
import dev.ultreon.devices.api.bios.FrameBufferInfo;
import dev.ultreon.devices.impl.bios.Bios;
import dev.ultreon.mineos.AppClassLoader;
import dev.ultreon.mineos.DriverManagerImpl;
import dev.ultreon.mineos.Executable;
import dev.ultreon.mineos.FSEntry;
import dev.ultreon.mineos.impl.Permission;
import dev.ultreon.mineos.userspace.FileSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import org.intellij.lang.annotations.Language;

import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;

public class MineOSKernel implements Kern {
    private static final DriverManagerImpl driverManager = new DriverManagerImpl();

    public void boot(Bios bios) {
        bios.enableInterrupts();

        this.loadDrivers(bios);
        this.enterUserspace(bios, Path.of("/usr/bin/logon.jar"));
    }

    private void loadDrivers(Bios bios) {
        FrameBufferInfo call = (FrameBufferInfo) bios.call(BiosCallType.FRAMEBUFFER_CALL, new Object[]{FrameBufferCall.GET_INFO, new Object[0]});

        if (call != null) {
            VGADriver driver = new VGADriver();
            driverManager.register("vga", driver);

            driver.load();
            driver.init(bios.getVEFISystem(), call.width, call.height, call.bpp);
        }
    }

    private void enterUserspace(Bios bios, Path mainPath) {
        @Language("jvm-class-name") final String className = "dev.ultreon.mineos.userspace.MineOS";

        try {
            AppClassLoader classLoader = new AppClassLoader(Thread.currentThread().getContextClassLoader(), new Executable(mainPath, this));
            classLoader.runMain(new String[]{});
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter userspace", e);
        }
    }

    public void playSound(SoundEvent sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1f, 1f));
    }

    @Override
    public void requestShutdown() throws SecurityException {

    }

    @Override
    public void requestReboot() throws SecurityException {

    }

    @Override
    public void requestHalt() throws SecurityException {

    }

    @Override
    public void askPermission(Permission permission, Mono<PermissionTicket> callback) throws SecurityException {

    }

    @Override
    public SeekableByteChannel openChannel(Path path) {
        return null;
    }

    @Override
    public void setFileSystem(FileSystem fileSystem) {

    }

    @Override
    public FSEntry getFileInfo(Path path) {
        return null;
    }

    @Override
    public SeekableByteChannel open(String path) {
        return null;
    }

    @Override
    public void close(SeekableByteChannel channel) {

    }
}
