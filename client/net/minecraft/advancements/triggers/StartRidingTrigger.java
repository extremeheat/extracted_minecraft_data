package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class StartRidingTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public StartRidingTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return StartRidingTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player) {
      this.trigger(player, (t) -> true);
   }

   public static record TriggerInstance(Optional<Holder<LootItemCondition>> player) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> playerStartsRiding(final EntityPredicate.Builder player) {
         return CriteriaTriggers.START_RIDING_TRIGGER.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(player))));
      }
   }
}
