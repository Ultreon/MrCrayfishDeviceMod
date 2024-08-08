package dev.ultreon.mineos.kernel;

import dev.ultreon.devices.api.bios.Bios;
import dev.ultreon.devices.api.bios.BiosCallType;
import dev.ultreon.devices.api.bios.FrameBufferCall;
import dev.ultreon.devices.api.bios.FrameBufferInfo;
import dev.ultreon.devices.impl.bios.Bios;
import dev.ultreon.mineos.DriverManagerImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import org.intellij.lang.annotations.Language;

public class MineOSKernel implements Kern {
    private static final DriverManagerImpl driverManager = new DriverManagerImpl();

    public void boot(Bios bios) {
        bios.enableInterrupts();

        this.loadDrivers(bios);

        this.enterUserspace(bios);
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

    private void enterUserspace(Bios bios) {
        @Language("jvm-class-name") final String className = "dev.ultreon.mineos.userspace.MineOS";

        try {
            bios.call(BiosCallType.ENTER_USERSPACE, new Object[] { className });
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter userspace", e);
        }
    }

    public void playSound(SoundEvent sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1f, 1f));
    }
}
