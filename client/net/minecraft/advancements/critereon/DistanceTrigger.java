package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class DistanceTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   final ResourceLocation id;

   public DistanceTrigger(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      LocationPredicate var4 = LocationPredicate.fromJson(var1.get("start_position"));
      DistancePredicate var5 = DistancePredicate.fromJson(var1.get("distance"));
      return new TriggerInstance(this.id, var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, Vec3 var2) {
      Vec3 var3 = var1.position();
      this.trigger(var1, (var3x) -> {
         return var3x.matches(var1.getLevel(), var2, var3);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final LocationPredicate startPosition;
      private final DistancePredicate distance;

      public TriggerInstance(ResourceLocation var1, EntityPredicate.Composite var2, LocationPredicate var3, DistancePredicate var4) {
         super(var1, var2);
         this.startPosition = var3;
         this.distance = var4;
      }

      public static TriggerInstance fallFromHeight(EntityPredicate.Builder var0, DistancePredicate var1, LocationPredicate var2) {
         return new TriggerInstance(CriteriaTriggers.FALL_FROM_HEIGHT.id, EntityPredicate.Composite.wrap(var0.build()), var2, var1);
      }

      public static TriggerInstance rideEntityInLava(EntityPredicate.Builder var0, DistancePredicate var1) {
         return new TriggerInstance(CriteriaTriggers.RIDE_ENTITY_IN_LAVA_TRIGGER.id, EntityPredicate.Composite.wrap(var0.build()), LocationPredicate.ANY, var1);
      }

      public static TriggerInstance travelledThroughNether(DistancePredicate var0) {
         return new TriggerInstance(CriteriaTriggers.NETHER_TRAVEL.id, EntityPredicate.Composite.ANY, LocationPredicate.ANY, var0);
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("start_position", this.startPosition.serializeToJson());
         var2.add("distance", this.distance.serializeToJson());
         return var2;
      }

      public boolean matches(ServerLevel var1, Vec3 var2, Vec3 var3) {
         if (!this.startPosition.matches(var1, var2.x, var2.y, var2.z)) {
            return false;
         } else {
            return this.distance.matches(var2.x, var2.y, var2.z, var3.x, var3.y, var3.z);
         }
      }
   }
}
