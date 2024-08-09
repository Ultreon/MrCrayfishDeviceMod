package dev.ultreon.devices.item;

import dev.ultreon.vbios.efi.VEFI_Executable;
import dev.ultreon.devices.impl.bios.VirtualDisk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;

public class InstallerFlashDriveItem extends FlashDriveItem {
    public InstallerFlashDriveItem(DyeColor color, ResourceLocation resourceLocation) {
        super(color);

        VirtualDisk disk = new VirtualDisk(Path.of("assets", resourceLocation.getNamespace(), "device_installers", resourceLocation.getPath() + ".jar"));
        try {
            disk.format();
        } catch (IOException e) {
            throw new RuntimeException(e);

        FileStorageHandlers.register(this, );
    }

    public VEFI_Executable getExecutable() {
        return executable;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack defaultInstance = super.getDefaultInstance();
        defaultInstance.getOrCreateTag().put("drive", );
        return defaultInstance;
    }
}
