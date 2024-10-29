package net.minecraft.world.entity.ai.targeting;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
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
   private Selector selector;

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

   public TargetingConditions selector(@Nullable Selector var1) {
      this.selector = var1;
      return this;
   }

   public boolean test(ServerLevel var1, @Nullable LivingEntity var2, LivingEntity var3) {
      if (var2 == var3) {
         return false;
      } else if (!var3.canBeSeenByAnyone()) {
         return false;
      } else if (this.selector != null && !this.selector.test(var3, var1)) {
         return false;
      } else {
         if (var2 == null) {
            if (this.isCombat && (!var3.canBeSeenAsEnemy() || var1.getDifficulty() == Difficulty.PEACEFUL)) {
               return false;
            }
         } else {
            if (this.isCombat && (!var2.canAttack(var3) || !var2.canAttackType(var3.getType()) || var2.isAlliedTo(var3))) {
               return false;
            }

            if (this.range > 0.0) {
               double var4 = this.testInvisible ? var3.getVisibilityPercent(var2) : 1.0;
               double var6 = Math.max(this.range * var4, 2.0);
               double var8 = var2.distanceToSqr(var3.getX(), var3.getY(), var3.getZ());
               if (var8 > var6 * var6) {
                  return false;
               }
            }

            if (this.checkLineOfSight && var2 instanceof Mob) {
               Mob var10 = (Mob)var2;
               if (!var10.getSensing().hasLineOfSight(var3)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   @FunctionalInterface
   public interface Selector {
      boolean test(LivingEntity var1, ServerLevel var2);
   }
}
