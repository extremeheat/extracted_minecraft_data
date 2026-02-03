package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;

public class ConstructBeaconTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public ConstructBeaconTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return ConstructBeaconTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final int levels) {
      this.trigger(player, (t) -> t.matches(levels));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, MinMaxBounds.Ints level) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), MinMaxBounds.Ints.CODEC.optionalFieldOf("level", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::level)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> constructedBeacon() {
         return CriteriaTriggers.CONSTRUCT_BEACON.createCriterion(new TriggerInstance(Optional.empty(), MinMaxBounds.Ints.ANY));
      }

      public static Criterion<TriggerInstance> constructedBeacon(final MinMaxBounds.Ints level) {
         return CriteriaTriggers.CONSTRUCT_BEACON.createCriterion(new TriggerInstance(Optional.empty(), level));
      }

      public boolean matches(final int levels) {
         return this.level.matches(levels);
      }
   }
}
