package com.ultreon.devices.init;

import com.ultreon.devices.Devices;
import com.ultreon.devices.Reference;
import dev.architectury.registry.registries.Registrar;
import net.minecraftforge.fml.RegistryObject;
import net.minecraft.util.Registry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/**
 * @author MrCrayfish
 */
public class DeviceSounds {
    private static final Registrar<SoundEvent> REGISTER = Devices.REGISTRIES.get().get(Registry.SOUND_EVENT_REGISTRY);

    public static final RegistryObject<SoundEvent> PRINTER_PRINTING = REGISTER.register(Devices.id("printer_printing"), () -> new SoundEvent(new ResourceLocation(Reference.MOD_ID, "printer_printing")));
    public static final RegistryObject<SoundEvent> PRINTER_LOADING_PAPER = REGISTER.register(Devices.id("printer_loading_paper"), () -> new SoundEvent(new ResourceLocation(Reference.MOD_ID, "printer_loading_paper")));

//    static {
//        PRINTER_PRINTING = registerSound("devices:printing_ink");
//        PRINTER_LOADING_PAPER = registerSound("devices:printing_paper");
//    }
//
//    private static SoundEvent registerSound(String soundNameIn) {
//        ResourceLocation resource = new ResourceLocation(soundNameIn);
//        return new SoundEvent(resource).setRegistryName(soundNameIn);
//    }

    public static void register() {
    }

//    @Mod.EventBusSubscriber(modid = Reference.MOD_ID)
//    public static class RegistrationHandler {
//        @SubscribeEvent
//        public static void registerSounds(final RegistryEvent.Register<SoundEvent> event) {
//            event.getRegistry().register(PRINTER_PRINTING);
//            event.getRegistry().register(PRINTER_LOADING_PAPER);
//        }
//    }
}
