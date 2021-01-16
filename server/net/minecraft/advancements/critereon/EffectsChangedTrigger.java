package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class EffectsChangedTrigger extends SimpleCriterionTrigger<EffectsChangedTrigger.TriggerInstance> {
   private static final ResourceLocation ID = new ResourceLocation("effects_changed");

   public EffectsChangedTrigger() {
      super();
   }

   public ResourceLocation getId() {
      return ID;
   }

   public EffectsChangedTrigger.TriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      MobEffectsPredicate var4 = MobEffectsPredicate.fromJson(var1.get("effects"));
      return new EffectsChangedTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1, (var1x) -> {
         return var1x.matches(var1);
      });
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, EntityPredicate.Composite var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MobEffectsPredicate effects;

      public TriggerInstance(EntityPredicate.Composite var1, MobEffectsPredicate var2) {
         super(EffectsChangedTrigger.ID, var1);
         this.effects = var2;
      }

      public static EffectsChangedTrigger.TriggerInstance hasEffects(MobEffectsPredicate var0) {
         return new EffectsChangedTrigger.TriggerInstance(EntityPredicate.Composite.ANY, var0);
      }

      public boolean matches(ServerPlayer var1) {
         return this.effects.matches((LivingEntity)var1);
      }

      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("effects", this.effects.serializeToJson());
         return var2;
      }
   }
}
