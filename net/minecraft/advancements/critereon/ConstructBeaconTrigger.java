package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class ConstructBeaconTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("construct_beacon");

   public ResourceLocation getId() {
      return ID;
   }

   public ConstructBeaconTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      MinMaxBounds.Ints var3 = MinMaxBounds.Ints.fromJson(var1.get("level"));
      return new ConstructBeaconTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, BeaconBlockEntity var2) {
      this.trigger(var1.getAdvancements(), (var1x) -> {
         return var1x.matches(var2);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints level;

      public TriggerInstance(MinMaxBounds.Ints var1) {
         super(ConstructBeaconTrigger.ID);
         this.level = var1;
      }

      public static ConstructBeaconTrigger.TriggerInstance constructedBeacon(MinMaxBounds.Ints var0) {
         return new ConstructBeaconTrigger.TriggerInstance(var0);
      }

      public boolean matches(BeaconBlockEntity var1) {
         return this.level.matches(var1.getLevels());
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("level", this.level.serializeToJson());
         return var1;
      }
   }
}
