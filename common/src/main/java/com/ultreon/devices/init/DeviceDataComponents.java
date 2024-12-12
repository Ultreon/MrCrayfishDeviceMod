package com.ultreon.devices.init;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.ultreon.devices.Devices;
import dev.ultreon.mods.xinexlib.platform.services.IRegistrar;
import dev.ultreon.mods.xinexlib.platform.services.IRegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public class DeviceDataComponents {
    private static final IRegistrar<DataComponentType<?>> REGISTER = Devices.REGISTRIES.get().getRegistrar(Registries.DATA_COMPONENT_TYPE);

    public static final IRegistrySupplier<DataComponentType<HardwareComponents>, DataComponentType<?>> HARDWARE_COMPONENTS = REGISTER.register("hardware_components", () -> new DataComponentType.Builder<HardwareComponents>().persistent(HardwareComponents.CODEC).build());
    public static final IRegistrySupplier<DataComponentType<CableData>, DataComponentType<?>> CABLE_DATA = REGISTER.register("cable_data", () -> new DataComponentType.Builder<CableData>().networkSynchronized(StreamCodec.of(
            CableData::write,
            CableData::read
    )).build());
    public static final IRegistrySupplier<DataComponentType<UUID>, DataComponentType<?>> DISK = REGISTER.register("disk", () -> new DataComponentType.Builder<UUID>().networkSynchronized(StreamCodec.of(
            (buf, id) -> buf.writeUUID(id),
            buf -> buf.readUUID()
    )).persistent(Codec.pair(Codec.LONG, Codec.LONG)
            .xmap(
                    pair -> new UUID(pair.getFirst(), pair.getSecond()),
                    uuid -> Pair.of(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits())
            )
    ).build());

    public static void register() {
        REGISTER.load();
    }
}
