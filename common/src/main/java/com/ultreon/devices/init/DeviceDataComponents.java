package com.ultreon.devices.init;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.ultreon.devices.Devices;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public class DeviceDataComponents {
    private static final Registrar<DataComponentType<?>> REGISTER = Devices.REGISTRIES.get().get(Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<HardwareComponents>> HARDWARE_COMPONENTS = REGISTER.register(Devices.id("hardware_components"), () -> new DataComponentType.Builder<HardwareComponents>().persistent(HardwareComponents.CODEC).build());
    public static final RegistrySupplier<DataComponentType<CableData>> CABLE_DATA = REGISTER.register(Devices.id("cable_data"), () -> new DataComponentType.Builder<CableData>().networkSynchronized(StreamCodec.of(
            CableData::write,
            CableData::read
    )).build());
    public static final RegistrySupplier<DataComponentType<UUID>> DISK = REGISTER.register(Devices.id("disk"), () -> new DataComponentType.Builder<UUID>().networkSynchronized(StreamCodec.of(
            (buf, id) -> buf.writeUUID(id),
            buf -> buf.readUUID()
    )).persistent(Codec.pair(Codec.LONG, Codec.LONG)
            .xmap(
                    pair -> new UUID(pair.getFirst(), pair.getSecond()),
                    uuid -> Pair.of(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits())
            )
    ).build());

    public static void register() {

    }
}
