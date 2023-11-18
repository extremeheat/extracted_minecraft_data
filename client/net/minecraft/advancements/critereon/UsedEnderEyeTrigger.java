package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

public class UsedEnderEyeTrigger extends SimpleCriterionTrigger<UsedEnderEyeTrigger.TriggerInstance> {
   public UsedEnderEyeTrigger() {
      super();
   }

   public UsedEnderEyeTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      MinMaxBounds.Doubles var4 = MinMaxBounds.Doubles.fromJson(var1.get("distance"));
      return new UsedEnderEyeTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, BlockPos var2) {
      double var3 = var1.getX() - (double)var2.getX();
      double var5 = var1.getZ() - (double)var2.getZ();
      double var7 = var3 * var3 + var5 * var5;
      this.trigger(var1, var2x -> var2x.matches(var7));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Doubles level;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, MinMaxBounds.Doubles var2) {
         super(var1);
         this.level = var2;
      }

      public boolean matches(double var1) {
         return this.level.matchesSqr(var1);
      }
   }
}
