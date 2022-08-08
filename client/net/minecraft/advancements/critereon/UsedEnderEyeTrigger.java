package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class UsedEnderEyeTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("used_ender_eye");

   public UsedEnderEyeTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      MinMaxBounds.Doubles var4 = MinMaxBounds.Doubles.fromJson(var1.get("distance"));
      return new TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, BlockPos var2) {
      double var3 = var1.getX() - (double)var2.getX();
      double var5 = var1.getZ() - (double)var2.getZ();
      double var7 = var3 * var3 + var5 * var5;
      this.trigger(var1, (var2x) -> {
         return var2x.matches(var7);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Doubles level;

      public TriggerInstance(EntityPredicate.Composite var1, MinMaxBounds.Doubles var2) {
         super(UsedEnderEyeTrigger.ID, var1);
         this.level = var2;
      }

      public boolean matches(double var1) {
         return this.level.matchesSqr(var1);
      }
   }
}
