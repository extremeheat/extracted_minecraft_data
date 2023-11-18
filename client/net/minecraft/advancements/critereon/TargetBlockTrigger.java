package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;

public class TargetBlockTrigger extends SimpleCriterionTrigger<TargetBlockTrigger.TriggerInstance> {
   public TargetBlockTrigger() {
      super();
   }

   public TargetBlockTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      MinMaxBounds.Ints var4 = MinMaxBounds.Ints.fromJson(var1.get("signal_strength"));
      Optional var5 = EntityPredicate.fromJson(var1, "projectile", var3);
      return new TargetBlockTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, Entity var2, Vec3 var3, int var4) {
      LootContext var5 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var3x -> var3x.matches(var5, var3, var4));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Ints signalStrength;
      private final Optional<ContextAwarePredicate> projectile;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, MinMaxBounds.Ints var2, Optional<ContextAwarePredicate> var3) {
         super(var1);
         this.signalStrength = var2;
         this.projectile = var3;
      }

      public static Criterion<TargetBlockTrigger.TriggerInstance> targetHit(MinMaxBounds.Ints var0, Optional<ContextAwarePredicate> var1) {
         return CriteriaTriggers.TARGET_BLOCK_HIT.createCriterion(new TargetBlockTrigger.TriggerInstance(Optional.empty(), var0, var1));
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         var1.add("signal_strength", this.signalStrength.serializeToJson());
         this.projectile.ifPresent(var1x -> var1.add("projectile", var1x.toJson()));
         return var1;
      }

      public boolean matches(LootContext var1, Vec3 var2, int var3) {
         if (!this.signalStrength.matches(var3)) {
            return false;
         } else {
            return !this.projectile.isPresent() || this.projectile.get().matches(var1);
         }
      }
   }
}
