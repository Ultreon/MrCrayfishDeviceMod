package com.ultreon.devices.init;

import com.ultreon.devices.advancements.critereon.LaptopUseTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;

import java.util.function.Supplier;

public class DeviceCriterionTriggers {
    public static final LaptopUseTrigger LAPTOP_USE = register("gamer_usage", LaptopUseTrigger::new);

    private static <T extends CriterionTrigger<?>> T register(String name, Supplier<T> supplier) {
        return CriteriaTriggers.register(supplier.get());
    }

    public static void register() {

    }
}
