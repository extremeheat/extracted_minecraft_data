package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ConstructBeaconTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("construct_beacon");

   public ConstructBeaconTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var1.get("level"));
      return new TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, int var2) {
      this.trigger(var1, (var1x) -> {
         return var1x.matches(var2);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints level;

      public TriggerInstance(EntityPredicate.Composite var1, MinMaxBounds.Ints var2) {
         super(ConstructBeaconTrigger.ID, var1);
         this.level = var2;
      }

      public static TriggerInstance constructedBeacon() {
         return new TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY);
      }

      public static TriggerInstance constructedBeacon(MinMaxBounds.Ints var0) {
         return new TriggerInstance(EntityPredicate.Composite.ANY, var0);
      }

      public boolean matches(int var1) {
         return this.level.matches(var1);
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("level", this.level.serializeToJson());
         return var2;
      }
   }
}
