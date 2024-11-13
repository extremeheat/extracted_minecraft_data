package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;

public class EffectsChangedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public EffectsChangedTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return EffectsChangedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, @Nullable Entity var2) {
      LootContext var3 = var2 != null ? EntityPredicate.createContext(var1, var2) : null;
      this.trigger(var1, (var2x) -> var2x.matches(var1, var3));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<MobEffectsPredicate> effects, Optional<ContextAwarePredicate> source) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), MobEffectsPredicate.CODEC.optionalFieldOf("effects").forGetter(TriggerInstance::effects), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("source").forGetter(TriggerInstance::source)).apply(var0, TriggerInstance::new));

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<MobEffectsPredicate> var2, Optional<ContextAwarePredicate> var3) {
         super();
         this.player = var1;
         this.effects = var2;
         this.source = var3;
      }

      public static Criterion<TriggerInstance> hasEffects(MobEffectsPredicate.Builder var0) {
         return CriteriaTriggers.EFFECTS_CHANGED.createCriterion(new TriggerInstance(Optional.empty(), var0.build(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> gotEffectsFrom(EntityPredicate.Builder var0) {
         return CriteriaTriggers.EFFECTS_CHANGED.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.wrap(var0.build()))));
      }

      public boolean matches(ServerPlayer var1, @Nullable LootContext var2) {
         if (this.effects.isPresent() && !((MobEffectsPredicate)this.effects.get()).matches((LivingEntity)var1)) {
            return false;
         } else {
            return !this.source.isPresent() || var2 != null && ((ContextAwarePredicate)this.source.get()).matches(var2);
         }
      }

      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.source, ".source");
      }
   }
}
