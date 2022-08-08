package net.minecraft.world.entity.ai.targeting;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class TargetingConditions {
   public static final TargetingConditions DEFAULT = forCombat();
   private static final double MIN_VISIBILITY_DISTANCE_FOR_INVISIBLE_TARGET = 2.0;
   private final boolean isCombat;
   private double range = -1.0;
   private boolean checkLineOfSight = true;
   private boolean testInvisible = true;
   @Nullable
   private Predicate<LivingEntity> selector;

   private TargetingConditions(boolean var1) {
      super();
      this.isCombat = var1;
   }

   public static TargetingConditions forCombat() {
      return new TargetingConditions(true);
   }

   public static TargetingConditions forNonCombat() {
      return new TargetingConditions(false);
   }

   public TargetingConditions copy() {
      TargetingConditions var1 = this.isCombat ? forCombat() : forNonCombat();
      var1.range = this.range;
      var1.checkLineOfSight = this.checkLineOfSight;
      var1.testInvisible = this.testInvisible;
      var1.selector = this.selector;
      return var1;
   }

   public TargetingConditions range(double var1) {
      this.range = var1;
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

   public TargetingConditions selector(@Nullable Predicate<LivingEntity> var1) {
      this.selector = var1;
      return this;
   }

   public boolean test(@Nullable LivingEntity var1, LivingEntity var2) {
      if (var1 == var2) {
         return false;
      } else if (!var2.canBeSeenByAnyone()) {
         return false;
      } else if (this.selector != null && !this.selector.test(var2)) {
         return false;
      } else {
         if (var1 == null) {
            if (this.isCombat && (!var2.canBeSeenAsEnemy() || var2.level.getDifficulty() == Difficulty.PEACEFUL)) {
               return false;
            }
         } else {
            if (this.isCombat && (!var1.canAttack(var2) || !var1.canAttackType(var2.getType()) || var1.isAlliedTo(var2))) {
               return false;
            }

            if (this.range > 0.0) {
               double var3 = this.testInvisible ? var2.getVisibilityPercent(var1) : 1.0;
               double var5 = Math.max(this.range * var3, 2.0);
               double var7 = var1.distanceToSqr(var2.getX(), var2.getY(), var2.getZ());
               if (var7 > var5 * var5) {
                  return false;
               }
            }

            if (this.checkLineOfSight && var1 instanceof Mob) {
               Mob var9 = (Mob)var1;
               if (!var9.getSensing().hasLineOfSight(var2)) {
                  return false;
               }
            }
         }

         return true;
      }
   }
}
