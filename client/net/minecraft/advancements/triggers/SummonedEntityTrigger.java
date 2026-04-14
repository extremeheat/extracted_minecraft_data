package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;

public class SummonedEntityTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public SummonedEntityTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return SummonedEntityTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Entity entity) {
      LootContext context = EntityPredicate.createContext(player, entity);
      this.trigger(player, (t) -> t.matches(context));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> entity) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> summonedEntity(final EntityPredicate.Builder predicate) {
         return CriteriaTriggers.SUMMONED_ENTITY.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(predicate))));
      }

      public boolean matches(final LootContext entity) {
         return this.entity.isEmpty() || ((ContextAwarePredicate)this.entity.get()).matches(entity);
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validate(validator.entityContext(), "entity", this.entity);
      }
   }
}
