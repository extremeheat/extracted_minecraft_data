package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class TameAnimalTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public TameAnimalTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return TameAnimalTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Animal animal) {
      LootContext animalContext = EntityPredicate.createContext(player, animal);
      this.trigger(player, (t) -> t.matches(animalContext));
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player, Optional<Holder<LootItemCondition>> entity) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), LootItemCondition.CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> tamedAnimal() {
         return CriteriaTriggers.TAME_ANIMAL.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> tamedAnimal(final EntityPredicate.Builder entity) {
         return CriteriaTriggers.TAME_ANIMAL.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(entity))));
      }

      public boolean matches(final LootContext animal) {
         return this.entity.isEmpty() || ((LootItemCondition)((Holder)this.entity.get()).value()).test(animal);
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validateHolder(validator.entityContext(), "entity", this.entity);
      }
   }
}
