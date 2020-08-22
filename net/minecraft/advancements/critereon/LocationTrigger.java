package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class LocationTrigger extends SimpleCriterionTrigger {
   private final ResourceLocation id;

   public LocationTrigger(ResourceLocation var1) {
      this.id = var1;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public LocationTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      LocationPredicate var3 = LocationPredicate.fromJson(var1);
      return new LocationTrigger.TriggerInstance(this.id, var3);
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1.getAdvancements(), (var1x) -> {
         return var1x.matches(var1.getLevel(), var1.getX(), var1.getY(), var1.getZ());
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate location;

      public TriggerInstance(ResourceLocation var1, LocationPredicate var2) {
         super(var1);
         this.location = var2;
      }

      public static LocationTrigger.TriggerInstance located(LocationPredicate var0) {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.LOCATION.id, var0);
      }

      public static LocationTrigger.TriggerInstance sleptInBed() {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.SLEPT_IN_BED.id, LocationPredicate.ANY);
      }

      public static LocationTrigger.TriggerInstance raidWon() {
         return new LocationTrigger.TriggerInstance(CriteriaTriggers.RAID_WIN.id, LocationPredicate.ANY);
      }

      public boolean matches(ServerLevel var1, double var2, double var4, double var6) {
         return this.location.matches(var1, var2, var4, var6);
      }

      public JsonElement serializeToJson() {
         return this.location.serializeToJson();
      }
   }
}
