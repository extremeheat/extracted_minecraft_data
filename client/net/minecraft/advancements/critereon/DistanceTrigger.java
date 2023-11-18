package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class DistanceTrigger extends SimpleCriterionTrigger<DistanceTrigger.TriggerInstance> {
   public DistanceTrigger() {
      super();
   }

   public DistanceTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = LocationPredicate.fromJson(var1.get("start_position"));
      Optional var5 = DistancePredicate.fromJson(var1.get("distance"));
      return new DistanceTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, Vec3 var2) {
      Vec3 var3 = var1.position();
      this.trigger(var1, var3x -> var3x.matches(var1.serverLevel(), var2, var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<LocationPredicate> startPosition;
      private final Optional<DistancePredicate> distance;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<LocationPredicate> var2, Optional<DistancePredicate> var3) {
         super(var1);
         this.startPosition = var2;
         this.distance = var3;
      }

      public static Criterion<DistanceTrigger.TriggerInstance> fallFromHeight(
         EntityPredicate.Builder var0, DistancePredicate var1, LocationPredicate.Builder var2
      ) {
         return CriteriaTriggers.FALL_FROM_HEIGHT
            .createCriterion(new DistanceTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(var0)), Optional.of(var2.build()), Optional.of(var1)));
      }

      public static Criterion<DistanceTrigger.TriggerInstance> rideEntityInLava(EntityPredicate.Builder var0, DistancePredicate var1) {
         return CriteriaTriggers.RIDE_ENTITY_IN_LAVA_TRIGGER
            .createCriterion(new DistanceTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(var0)), Optional.empty(), Optional.of(var1)));
      }

      public static Criterion<DistanceTrigger.TriggerInstance> travelledThroughNether(DistancePredicate var0) {
         return CriteriaTriggers.NETHER_TRAVEL.createCriterion(new DistanceTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(var0)));
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.startPosition.ifPresent(var1x -> var1.add("start_position", var1x.serializeToJson()));
         this.distance.ifPresent(var1x -> var1.add("distance", var1x.serializeToJson()));
         return var1;
      }

      public boolean matches(ServerLevel var1, Vec3 var2, Vec3 var3) {
         if (this.startPosition.isPresent() && !this.startPosition.get().matches(var1, var2.x, var2.y, var2.z)) {
            return false;
         } else {
            return !this.distance.isPresent() || this.distance.get().matches(var2.x, var2.y, var2.z, var3.x, var3.y, var3.z);
         }
      }
   }
}
