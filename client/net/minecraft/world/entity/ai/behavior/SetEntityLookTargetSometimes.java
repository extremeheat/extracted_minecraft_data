package net.minecraft.world.entity.ai.behavior;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;

/** @deprecated */
@Deprecated
public class SetEntityLookTargetSometimes {
   public SetEntityLookTargetSometimes() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(final float maxDist, final UniformInt interval) {
      return create(maxDist, interval, (mob) -> true);
   }

   public static BehaviorControl<LivingEntity> create(final EntityType<?> type, final float maxDist, final UniformInt interval) {
      return create(maxDist, interval, (mob) -> mob.is(type));
   }

   private static BehaviorControl<LivingEntity> create(final float maxDist, final UniformInt interval, final Predicate<LivingEntity> predicate) {
      float maxDistSqr = maxDist * maxDist;
      Ticker ticker = new Ticker(interval);
      return BehaviorBuilder.create((Function)((i) -> i.group(i.absent(MemoryModuleType.LOOK_TARGET), i.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(i, (lookTarget, nearestEntities) -> (level, body, timestamp) -> {
               Optional<Entity> target = ((NearestVisibleLivingEntities)i.get(nearestEntities)).findClosestMatchingLivingEntityPredicate(predicate.and((mob) -> mob.distanceToSqr(body) <= (double)maxDistSqr));
               if (target.isEmpty()) {
                  return false;
               } else if (!ticker.tickDownAndCheck(level.getRandom())) {
                  return false;
               } else {
                  lookTarget.set(new EntityTracker((Entity)target.get(), true));
                  return true;
               }
            })));
   }

   public static final class Ticker {
      private final UniformInt interval;
      private int ticksUntilNextStart;

      public Ticker(final UniformInt interval) {
         super();
         if (interval.minInclusive() <= 1) {
            throw new IllegalArgumentException();
         } else {
            this.interval = interval;
         }
      }

      public boolean tickDownAndCheck(final RandomSource random) {
         if (this.ticksUntilNextStart == 0) {
            this.ticksUntilNextStart = this.interval.sample(random) - 1;
            return false;
         } else {
            return --this.ticksUntilNextStart == 0;
         }
      }
   }
}
