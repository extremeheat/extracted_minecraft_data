package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class UsedEnderEyeTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("used_ender_eye");

   public ResourceLocation getId() {
      return ID;
   }

   public UsedEnderEyeTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      MinMaxBounds.Floats var3 = MinMaxBounds.Floats.fromJson(var1.get("distance"));
      return new UsedEnderEyeTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1, BlockPos var2) {
      double var3 = var1.getX() - (double)var2.getX();
      double var5 = var1.getZ() - (double)var2.getZ();
      double var7 = var3 * var3 + var5 * var5;
      this.trigger(var1.getAdvancements(), (var2x) -> {
         return var2x.matches(var7);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MinMaxBounds.Floats level;

      public TriggerInstance(MinMaxBounds.Floats var1) {
         super(UsedEnderEyeTrigger.ID);
         this.level = var1;
      }

      public boolean matches(double var1) {
         return this.level.matchesSqr(var1);
      }
   }
}
