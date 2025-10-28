package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.component.KineticWeapon;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SpearUseGoal<T extends Monster> extends Goal {
   private final T mob;
   private final double speedModifierWhenCharging;
   private final double speedModifierWhenRepositioning;
   private final double attackRadiusSqr;
   private final double targetInRangeSqr;
   private static final double MAX_FLEEING_TIME = (double)reducedTickDelay(100);
   private int engageTime = -1;
   private int fleeingTime = -1;
   private @Nullable Vec3 awayPos;
   private boolean done = false;

   public SpearUseGoal(T var1, double var2, double var4, float var6, float var7) {
      super();
      this.mob = var1;
      this.speedModifierWhenCharging = var2;
      this.speedModifierWhenRepositioning = var4;
      this.attackRadiusSqr = (double)(var6 * var6);
      this.targetInRangeSqr = (double)(var7 * var7);
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      return this.ableToAttack() && !this.mob.isUsingItem();
   }

   private boolean ableToAttack() {
      return this.mob.getTarget() != null && this.mob.getMainHandItem().has(DataComponents.KINETIC_WEAPON);
   }

   private int getKineticWeaponUseDuration() {
      int var1 = (Integer)Optional.ofNullable((KineticWeapon)this.mob.getMainHandItem().get(DataComponents.KINETIC_WEAPON)).map(KineticWeapon::computeDamageUseDuration).orElse(0);
      return reducedTickDelay(var1);
   }

   public boolean canContinueToUse() {
      return !this.done && this.ableToAttack();
   }

   public void start() {
      super.start();
      this.mob.setAggressive(true);
   }

   public void stop() {
      super.stop();
      this.mob.getNavigation().stop();
      this.mob.setAggressive(false);
      this.engageTime = -1;
      this.fleeingTime = -1;
      this.awayPos = null;
      this.done = false;
      this.mob.stopUsingItem();
   }

   public void tick() {
      LivingEntity var1 = this.mob.getTarget();
      double var2 = this.mob.distanceToSqr(var1.getX(), var1.getY(), var1.getZ());
      Entity var4 = this.mob.getRootVehicle();
      float var5 = 1.0F;
      if (var4 instanceof Mob var6) {
         var5 = var6.chargeSpeedModifier();
      }

      this.mob.lookAt(var1, 30.0F, 30.0F);
      this.mob.getLookControl().setLookAt(var1, 30.0F, 30.0F);
      if (this.engageTime < 0) {
         if (var2 > this.attackRadiusSqr) {
            this.mob.getNavigation().moveTo((Entity)var1, (double)var5 * this.speedModifierWhenRepositioning);
            return;
         }

         this.engageTime = this.getKineticWeaponUseDuration();
         this.mob.startUsingItem(InteractionHand.MAIN_HAND);
      }

      if (this.engageTime > 0) {
         --this.engageTime;
         if (this.engageTime == 0) {
            this.mob.stopUsingItem();
            double var8 = Math.sqrt(var2);
            this.awayPos = LandRandomPos.getPosAway(this.mob, Math.max(0.0, 10.0 - var8), Math.max(1.0, 14.0 - var8), 7, var1.position());
            this.fleeingTime = 1;
         }
      }

      if (this.fleeingTime > 0) {
         ++this.fleeingTime;
         if ((double)this.fleeingTime > MAX_FLEEING_TIME) {
            this.done = true;
            return;
         }
      }

      if (this.awayPos != null) {
         this.mob.getNavigation().moveTo(this.awayPos.x, this.awayPos.y, this.awayPos.z, (double)var5 * this.speedModifierWhenRepositioning);
         if (this.mob.getNavigation().isDone()) {
            if (this.fleeingTime > 0) {
               this.done = true;
               return;
            }

            this.awayPos = null;
         }
      } else {
         this.mob.getNavigation().moveTo((Entity)var1, (double)var5 * this.speedModifierWhenCharging);
         if (var2 < this.targetInRangeSqr || this.mob.getNavigation().isDone()) {
            double var9 = Math.sqrt(var2);
            this.awayPos = LandRandomPos.getPosAway(this.mob, 6.0 - var9, 9.0 - var9, 7, var1.position());
         }
      }

   }
}
