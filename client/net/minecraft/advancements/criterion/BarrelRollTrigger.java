package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class BarrelRollTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public BarrelRollTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return BarrelRollTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player) {
      this.trigger(player, (var0) -> true);
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> barrelRolled() {
         return CriteriaTriggers.BARREL_ROLL.createCriterion(new TriggerInstance(Optional.empty()));
      }

      public static Criterion<TriggerInstance> barrelRolledInNether() {
         return CriteriaTriggers.BARREL_ROLL.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().located(LocationPredicate.Builder.inDimension(Level.NETHER))))));
      }

      public static Criterion<TriggerInstance> barrelRolledInEnd() {
         return CriteriaTriggers.BARREL_ROLL.createCriterion(new TriggerInstance(Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().located(LocationPredicate.Builder.inDimension(Level.END))))));
      }
   }
}
