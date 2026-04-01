package net.minecraft.world.entity.ai.targeting;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Targetable;
import org.jspecify.annotations.Nullable;

public class TargetingConditions {
   public static final TargetingConditions DEFAULT = forCombat();
   private static final double MIN_VISIBILITY_DISTANCE_FOR_INVISIBLE_TARGET = 2.0;
   private final boolean isCombat;
   private double range = -1.0;
   private boolean checkLineOfSight = true;
   private boolean testInvisible = true;
   protected @Nullable Selector selector;

   protected TargetingConditions(final boolean isCombat) {
      super();
      this.isCombat = isCombat;
   }

   public static TargetingConditions forCombat() {
      return new TargetingConditions(true);
   }

   public static TargetingConditions forNonCombat() {
      return new TargetingConditions(false);
   }

   public static TargetingConditions livingBlock() {
      return new LivingBlockTargetingConditions();
   }

   public TargetingConditions copy() {
      TargetingConditions clone = this.isCombat ? forCombat() : forNonCombat();
      clone.range = this.range;
      clone.checkLineOfSight = this.checkLineOfSight;
      clone.testInvisible = this.testInvisible;
      clone.selector = this.selector;
      return clone;
   }

   public TargetingConditions range(final double range) {
      this.range = range;
      return this;
   }

   public TargetingConditions ignoreLineOfSight() {
      this.checkLineOfSight = false;
      return this;
   }

   public TargetingConditions ignoreInvisibilityTesting() {
      this.testInvisible = false;
      return this;
   }

   public TargetingConditions selector(final @Nullable Selector selector) {
      this.selector = selector;
      return this;
   }

   public <T extends Entity & Targetable> boolean test(final ServerLevel level, final @Nullable Entity targeter, final Entity target) {
      if (targeter instanceof LivingEntity livingEntity) {
         if (target instanceof Targetable) {
            if (livingEntity == target) {
               return false;
            }

            if (!((Targetable)target).canBeSeenByAnyone()) {
               return false;
            }

            if (this.selector != null && !this.selector.test(target, level)) {
               return false;
            }

            if (livingEntity == null) {
               if (this.isCombat && (!((Targetable)target).canBeSeenAsEnemy() || level.getDifficulty() == Difficulty.PEACEFUL)) {
                  return false;
               }
            } else {
               if (this.isCombat && (!livingEntity.canAttack(target) || livingEntity.isAlliedTo(target))) {
                  return false;
               }

               if (this.range > 0.0) {
                  double modifier = this.testInvisible ? ((Targetable)target).getVisibilityPercent(livingEntity) : 1.0;
                  double visibilityDistance = Math.max(this.range * modifier, 2.0);
                  double distanceToSqr = livingEntity.distanceToSqr(target.getX(), target.getY(), target.getZ());
                  if (distanceToSqr > visibilityDistance * visibilityDistance) {
                     return false;
                  }
               }

               if (this.checkLineOfSight && livingEntity instanceof Mob) {
                  Mob mob = (Mob)livingEntity;
                  if (!mob.getSensing().hasLineOfSight(target)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      return false;
   }

   @FunctionalInterface
   public interface Selector {
      boolean test(Entity target, ServerLevel level);
   }
}
