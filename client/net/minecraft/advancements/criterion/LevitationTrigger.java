package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class LevitationTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public LevitationTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return LevitationTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Vec3 start, final int duration) {
      this.trigger(player, (t) -> t.matches(player, start, duration));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<DistancePredicate> distance, MinMaxBounds.Ints duration) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(TriggerInstance::distance), MinMaxBounds.Ints.CODEC.optionalFieldOf("duration", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::duration)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> levitated(final DistancePredicate distance) {
         return CriteriaTriggers.LEVITATION.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(distance), MinMaxBounds.Ints.ANY));
      }

      public boolean matches(final ServerPlayer player, final Vec3 start, final int duration) {
         if (this.distance.isPresent() && !((DistancePredicate)this.distance.get()).matches(start.x, start.y, start.z, player.getX(), player.getY(), player.getZ())) {
            return false;
         } else {
            return this.duration.matches(duration);
         }
      }
   }
}
