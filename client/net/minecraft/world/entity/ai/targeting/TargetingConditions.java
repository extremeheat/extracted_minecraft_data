package net.minecraft.world.entity.ai.targeting;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class TargetingConditions {
   public static final TargetingConditions DEFAULT = new TargetingConditions();
   private double range = -1.0D;
   private boolean allowInvulnerable;
   private boolean allowSameTeam;
   private boolean allowUnseeable;
   private boolean allowNonAttackable;
   private boolean testInvisible = true;
   private Predicate<LivingEntity> selector;

   public TargetingConditions() {
      super();
   }

   public TargetingConditions range(double var1) {
      this.range = var1;
      return this;
   }

   public TargetingConditions allowInvulnerable() {
      this.allowInvulnerable = true;
      return this;
   }

   public TargetingConditions allowSameTeam() {
      this.allowSameTeam = true;
      return this;
   }

   public TargetingConditions allowUnseeable() {
      this.allowUnseeable = true;
      return this;
   }

   public TargetingConditions allowNonAttackable() {
      this.allowNonAttackable = true;
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
      } else if (var2.isSpectator()) {
         return false;
      } else if (!var2.isAlive()) {
         return false;
      } else if (!this.allowInvulnerable && var2.isInvulnerable()) {
         return false;
      } else if (this.selector != null && !this.selector.test(var2)) {
         return false;
      } else {
         if (var1 != null) {
            if (!this.allowNonAttackable) {
               if (!var1.canAttack(var2)) {
                  return false;
               }

               if (!var1.canAttackType(var2.getType())) {
                  return false;
               }
            }

            if (!this.allowSameTeam && var1.isAlliedTo(var2)) {
               return false;
            }

            if (this.range > 0.0D) {
               double var3 = this.testInvisible ? var2.getVisibilityPercent(var1) : 1.0D;
               double var5 = this.range * var3;
               double var7 = var1.distanceToSqr(var2.x, var2.y, var2.z);
               if (var7 > var5 * var5) {
                  return false;
               }
            }

            if (!this.allowUnseeable && var1 instanceof Mob && !((Mob)var1).getSensing().canSee(var2)) {
               return false;
            }
         }

         return true;
      }
   }
}
