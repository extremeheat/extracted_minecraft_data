package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger extends SimpleCriterionTrigger<LevitationTrigger.TriggerInstance> {
   public LevitationTrigger() {
      super();
   }

   public LevitationTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = DistancePredicate.fromJson(var1.get("distance"));
      MinMaxBounds.Ints var5 = MinMaxBounds.Ints.fromJson(var1.get("duration"));
      return new LevitationTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, Vec3 var2, int var3) {
      this.trigger(var1, var3x -> var3x.matches(var1, var2, var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<DistancePredicate> distance;
      private final MinMaxBounds.Ints duration;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<DistancePredicate> var2, MinMaxBounds.Ints var3) {
         super(var1);
         this.distance = var2;
         this.duration = var3;
      }

      public static Criterion<LevitationTrigger.TriggerInstance> levitated(DistancePredicate var0) {
         return CriteriaTriggers.LEVITATION.createCriterion(new LevitationTrigger.TriggerInstance(Optional.empty(), Optional.of(var0), MinMaxBounds.Ints.ANY));
      }

      public boolean matches(ServerPlayer var1, Vec3 var2, int var3) {
         if (this.distance.isPresent() && !this.distance.get().matches(var2.x, var2.y, var2.z, var1.getX(), var1.getY(), var1.getZ())) {
            return false;
         } else {
            return this.duration.matches(var3);
         }
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.distance.ifPresent(var1x -> var1.add("distance", var1x.serializeToJson()));
         var1.add("duration", this.duration.serializeToJson());
         return var1;
      }
   }
}
