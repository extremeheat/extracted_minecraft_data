package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContextSource;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FallAfterExplosionTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public FallAfterExplosionTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return FallAfterExplosionTrigger.TriggerInstance.CODEC;
   }

   public void trigger(final ServerPlayer player, final Vec3 startPosition, final @Nullable Entity cause) {
      Vec3 playerPosition = player.position();
      LootContext wrappedCause = cause != null ? EntityPredicate.createContext(player, cause) : null;
      this.trigger(player, (t) -> t.matches(player.level(), startPosition, playerPosition, wrappedCause));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<LocationPredicate> startPosition, Optional<DistancePredicate> distance, Optional<ContextAwarePredicate> cause) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((i) -> i.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), LocationPredicate.CODEC.optionalFieldOf("start_position").forGetter(TriggerInstance::startPosition), DistancePredicate.CODEC.optionalFieldOf("distance").forGetter(TriggerInstance::distance), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("cause").forGetter(TriggerInstance::cause)).apply(i, TriggerInstance::new));

      public TriggerInstance {
         super();
      }

      public static Criterion<TriggerInstance> fallAfterExplosion(final DistancePredicate distance, final EntityPredicate.Builder cause) {
         return CriteriaTriggers.FALL_AFTER_EXPLOSION.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(distance), Optional.of(EntityPredicate.wrap(cause))));
      }

      public void validate(final ValidationContextSource validator) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(validator);
         Validatable.validate(validator.entityContext(), "cause", this.cause);
      }

      public boolean matches(final ServerLevel level, final Vec3 enteredPosition, final Vec3 playerPosition, final @Nullable LootContext cause) {
         if (this.startPosition.isPresent() && !((LocationPredicate)this.startPosition.get()).matches(level, enteredPosition.x, enteredPosition.y, enteredPosition.z)) {
            return false;
         } else if (this.distance.isPresent() && !((DistancePredicate)this.distance.get()).matches(enteredPosition.x, enteredPosition.y, enteredPosition.z, playerPosition.x, playerPosition.y, playerPosition.z)) {
            return false;
         } else {
            return !this.cause.isPresent() || cause != null && ((ContextAwarePredicate)this.cause.get()).matches(cause);
         }
      }
   }
}
