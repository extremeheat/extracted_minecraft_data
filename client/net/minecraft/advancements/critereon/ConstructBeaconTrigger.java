package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ConstructBeaconTrigger extends SimpleCriterionTrigger<ConstructBeaconTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("construct_beacon");

   public ConstructBeaconTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public ConstructBeaconTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var1.get("level"));
      return new ConstructBeaconTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, int var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints level;

      public TriggerInstance(ContextAwarePredicate var1, MinMaxBounds.Ints var2) {
         super(ConstructBeaconTrigger.ID, var1);
         this.level = var2;
      }

      public static ConstructBeaconTrigger.TriggerInstance constructedBeacon() {
         return new ConstructBeaconTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY);
      }

      public static ConstructBeaconTrigger.TriggerInstance constructedBeacon(MinMaxBounds.Ints var0) {
         return new ConstructBeaconTrigger.TriggerInstance(ContextAwarePredicate.ANY, var0);
      }

      public boolean matches(int var1) {
         return this.level.matches(var1);
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("level", this.level.serializeToJson());
         return var2;
      }
   }
}
