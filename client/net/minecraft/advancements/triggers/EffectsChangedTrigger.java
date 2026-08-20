package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.MobEffectsPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.Nullable;

public class EffectsChangedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public EffectsChangedTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return EffectsChangedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final @Nullable Entity source) {
      LootContext wrappedSource = source != null ? EntityPredicate.createContext(player, source) : null;
      this.trigger(player, (t) -> t.matches(player, wrappedSource));
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, Optional<MobEffectsPredicate> effects, Optional<Holder<LootItemCondition>> source) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), MobEffectsPredicate.CODEC.optionalFieldOf("effects").forGetter(TriggerInstance::effects), LootItemCondition.CODEC.optionalFieldOf("source").forGetter(TriggerInstance::source)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> hasEffects(final MobEffectsPredicate.Builder effects) {
         return CriteriaTriggers.EFFECTS_CHANGED.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(effects.build()), Optional.empty()));
      }

      public static Criterion<TriggerInstance> gotEffectsFrom(final EntityPredicate.Builder source) {
         return CriteriaTriggers.EFFECTS_CHANGED.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.wrap(source.build()))));
      }

      public boolean matches(final ServerPlayer player, final @Nullable LootContext source) {
         if (this.effects.isPresent() && !((MobEffectsPredicate)this.effects.get()).matches((LivingEntity)player)) {
            return false;
         } else {
            return !this.source.isPresent() || source != null && ((LootItemCondition)((Holder)this.source.get()).value()).test(source);
         }
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validateHolder(validator.entityContext(), "source", this.source);
      }
   }
}
