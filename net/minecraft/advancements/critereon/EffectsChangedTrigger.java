package net.minecraft.advancements.critereon;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class EffectsChangedTrigger extends SimpleCriterionTrigger {
   private static final ResourceLocation ID = new ResourceLocation("effects_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public EffectsChangedTrigger.TriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      MobEffectsPredicate var3 = MobEffectsPredicate.fromJson(var1.get("effects"));
      return new EffectsChangedTrigger.TriggerInstance(var3);
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1.getAdvancements(), (var1x) -> {
         return var1x.matches(var1);
      });
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2) {
      return this.createInstance(var1, var2);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MobEffectsPredicate effects;

      public TriggerInstance(MobEffectsPredicate var1) {
         super(EffectsChangedTrigger.ID);
         this.effects = var1;
      }

      public static EffectsChangedTrigger.TriggerInstance hasEffects(MobEffectsPredicate var0) {
         return new EffectsChangedTrigger.TriggerInstance(var0);
      }

      public boolean matches(ServerPlayer var1) {
         return this.effects.matches((LivingEntity)var1);
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("effects", this.effects.serializeToJson());
         return var1;
      }
   }
}
