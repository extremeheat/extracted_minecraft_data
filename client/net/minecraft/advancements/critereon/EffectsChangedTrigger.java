package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;

public class EffectsChangedTrigger extends SimpleCriterionTrigger<EffectsChangedTrigger.TriggerInstance> {
   public EffectsChangedTrigger() {
      super();
   }

   public EffectsChangedTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = MobEffectsPredicate.fromJson(var1.get("effects"));
      Optional var5 = EntityPredicate.fromJson(var1, "source", var3);
      return new EffectsChangedTrigger.TriggerInstance(var2, var4, var5);
   }

   public void trigger(ServerPlayer var1, @Nullable Entity var2) {
      LootContext var3 = var2 != null ? EntityPredicate.createContext(var1, var2) : null;
      this.trigger(var1, var2x -> var2x.matches(var1, var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<MobEffectsPredicate> effects;
      private final Optional<ContextAwarePredicate> source;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<MobEffectsPredicate> var2, Optional<ContextAwarePredicate> var3) {
         super(var1);
         this.effects = var2;
         this.source = var3;
      }

      public static Criterion<EffectsChangedTrigger.TriggerInstance> hasEffects(MobEffectsPredicate.Builder var0) {
         return CriteriaTriggers.EFFECTS_CHANGED.createCriterion(new EffectsChangedTrigger.TriggerInstance(Optional.empty(), var0.build(), Optional.empty()));
      }

      public static Criterion<EffectsChangedTrigger.TriggerInstance> gotEffectsFrom(EntityPredicate.Builder var0) {
         return CriteriaTriggers.EFFECTS_CHANGED
            .createCriterion(new EffectsChangedTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.wrap(var0.build()))));
      }

      public boolean matches(ServerPlayer var1, @Nullable LootContext var2) {
         if (this.effects.isPresent() && !this.effects.get().matches((LivingEntity)var1)) {
            return false;
         } else {
            return !this.source.isPresent() || var2 != null && this.source.get().matches(var2);
         }
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.effects.ifPresent(var1x -> var1.add("effects", var1x.serializeToJson()));
         this.source.ifPresent(var1x -> var1.add("source", var1x.toJson()));
         return var1;
      }
   }
}
