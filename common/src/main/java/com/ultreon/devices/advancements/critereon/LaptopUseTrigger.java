//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ultreon.devices.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.ultreon.devices.Devices;
import com.ultreon.devices.util.TimeUtils;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class LaptopUseTrigger extends SimpleCriterionTrigger<LaptopUseTrigger.TriggerInstance> {
    private static final ResourceLocation ID = Devices.id("laptop_use");

    public LaptopUseTrigger() {
    }

    @NotNull
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    protected TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite player, DeserializationContext context) {
        JsonPrimitive minutesJson = json.getAsJsonPrimitive("minutes");
        int minutes = minutesJson.getAsInt();
        return new TriggerInstance(minutes);
    }

    public void trigger(ServerPlayer player, int ticks) {
        this.trigger(player, triggerInstance -> triggerInstance.matchesTicks(ticks));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final int minutes;

        public TriggerInstance(int minutes) {
            super(LaptopUseTrigger.ID, EntityPredicate.Composite.ANY);
            this.minutes = minutes;
        }

        @NotNull
        public ResourceLocation getCriterion() {
            return LaptopUseTrigger.ID;
        }

        @NotNull
        public JsonObject serializeToJson(@NotNull SerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("minutes", minutes);
            return json;
        }

        public int getMinutes() {
            return minutes;
        }

        public boolean matchesTicks(int ticks) {
            return ticks >= TimeUtils.minutesToTicks(minutes);
        }

        public boolean matchesMinutes(int minutes) {
            return minutes >= TimeUtils.minutesToTicks(this.minutes);
        }
    }
}
