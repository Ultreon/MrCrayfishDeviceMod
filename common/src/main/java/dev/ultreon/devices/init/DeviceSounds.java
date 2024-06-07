package dev.ultreon.devices.init;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.devices.UltreonDevicesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

/**
 * @author MrCrayfish
 */
public class DeviceSounds {
    private static final Registrar<SoundEvent> REGISTER = UltreonDevicesMod.REGISTRIES.get().get(Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> PRINTER_PRINTING = REGISTER.register(UltreonDevicesMod.id("printer_printing"), () -> SoundEvent.createVariableRangeEvent(UltreonDevicesMod.id("printer_printing")));
    public static final RegistrySupplier<SoundEvent> PRINTER_LOADING_PAPER = REGISTER.register(UltreonDevicesMod.id("printer_loading_paper"), () -> SoundEvent.createVariableRangeEvent(UltreonDevicesMod.id("printer_loading_paper")));

    public static void register() {

    }
}
