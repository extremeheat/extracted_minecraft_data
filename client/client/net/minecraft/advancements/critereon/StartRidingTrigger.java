package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;

public class StartRidingTrigger extends SimpleCriterionTrigger<StartRidingTrigger.TriggerInstance> {
   public StartRidingTrigger() {
      super();
   }

   @Override
   public Codec<StartRidingTrigger.TriggerInstance> codec() {
      return StartRidingTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1) {
      this.trigger(var1, var0 -> true);
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<StartRidingTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(StartRidingTrigger.TriggerInstance::player))
               .apply(var0, StartRidingTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> player) {
         super();
         this.player = player;
      }

      public static Criterion<StartRidingTrigger.TriggerInstance> playerStartsRiding(EntityPredicate.Builder var0) {
         return CriteriaTriggers.START_RIDING_TRIGGER.createCriterion(new StartRidingTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(var0))));
      }
   }
}
