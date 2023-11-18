package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;

public class EffectsChangedTrigger extends SimpleCriterionTrigger<EffectsChangedTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("effects_changed");

   public EffectsChangedTrigger() {
      super();
   }

   @Override
   public ResourceLocation getId() {
      return ID;
   }

   public EffectsChangedTrigger.TriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      MobEffectsPredicate var4 = MobEffectsPredicate.fromJson(var1.get("effects"));
      ContextAwarePredicate var5 = EntityPredicate.fromJson(var1, "source", var3);
      return new EffectsChangedTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, @Nullable Entity var2) {
      LootContext var3 = var2 != null ? EntityPredicate.createContext(var1, var2) : null;
      this.trigger(var1, var2x -> var2x.matches(var1, var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MobEffectsPredicate effects;
      private final ContextAwarePredicate source;

      public TriggerInstance(ContextAwarePredicate var1, MobEffectsPredicate var2, ContextAwarePredicate var3) {
         super(EffectsChangedTrigger.ID, var1);
         this.effects = var2;
         this.source = var3;
      }

      public static EffectsChangedTrigger.TriggerInstance hasEffects(MobEffectsPredicate var0) {
         return new EffectsChangedTrigger.TriggerInstance(ContextAwarePredicate.ANY, var0, ContextAwarePredicate.ANY);
      }

      public static EffectsChangedTrigger.TriggerInstance gotEffectsFrom(EntityPredicate var0) {
         return new EffectsChangedTrigger.TriggerInstance(ContextAwarePredicate.ANY, MobEffectsPredicate.ANY, EntityPredicate.wrap(var0));
      }

      public boolean matches(ServerPlayer var1, @Nullable LootContext var2) {
         if (!this.effects.matches((LivingEntity)var1)) {
            return false;
         } else {
            return this.source == ContextAwarePredicate.ANY || var2 != null && this.source.matches(var2);
         }
      }

      @Override
      public JsonObject serializeToJson(SerializationContext var1) {
         JsonObject var2 = super.serializeToJson(var1);
         var2.add("effects", this.effects.serializeToJson());
         var2.add("source", this.source.toJson(var1));
         return var2;
      }
   }
}
