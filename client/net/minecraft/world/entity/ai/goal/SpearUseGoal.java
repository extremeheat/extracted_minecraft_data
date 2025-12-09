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
   static final int MIN_REPOSITION_DISTANCE = 6;
   static final int MAX_REPOSITION_DISTANCE = 7;
   static final int MIN_COOLDOWN_DISTANCE = 9;
   static final int MAX_COOLDOWN_DISTANCE = 11;
   static final double MAX_FLEEING_TIME = (double)reducedTickDelay(100);
   private final T mob;
   private @Nullable SpearUseState state;
   double speedModifierWhenCharging;
   double speedModifierWhenRepositioning;
   float approachDistanceSq;
   float targetInRangeRadiusSq;

   public SpearUseGoal(T var1, double var2, double var4, float var6, float var7) {
      super();
      this.mob = var1;
      this.speedModifierWhenCharging = var2;
      this.speedModifierWhenRepositioning = var4;
      this.approachDistanceSq = var6 * var6;
      this.targetInRangeRadiusSq = var7 * var7;
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
      return this.state != null && !this.state.done && this.ableToAttack();
   }

   public void start() {
      super.start();
      this.mob.setAggressive(true);
      this.state = new SpearUseState();
   }

   public void stop() {
      super.stop();
      this.mob.getNavigation().stop();
      this.mob.setAggressive(false);
      this.state = null;
      this.mob.stopUsingItem();
   }

   public void tick() {
      if (this.state != null) {
         LivingEntity var1 = this.mob.getTarget();
         double var2 = this.mob.distanceToSqr(var1.getX(), var1.getY(), var1.getZ());
         Entity var4 = this.mob.getRootVehicle();
         float var5 = 1.0F;
         if (var4 instanceof Mob) {
            Mob var6 = (Mob)var4;
            var5 = var6.chargeSpeedModifier();
         }

         int var9 = this.mob.isPassenger() ? 2 : 0;
         this.mob.lookAt(var1, 30.0F, 30.0F);
         this.mob.getLookControl().setLookAt(var1, 30.0F, 30.0F);
         if (this.state.notEngagedYet()) {
            if (var2 > (double)this.approachDistanceSq) {
               this.mob.getNavigation().moveTo((Entity)var1, (double)var5 * this.speedModifierWhenRepositioning);
               return;
            }

            this.state.startEngagement(this.getKineticWeaponUseDuration());
            this.mob.startUsingItem(InteractionHand.MAIN_HAND);
         }

         if (this.state.tickAndCheckEngagement()) {
            this.mob.stopUsingItem();
            double var7 = Math.sqrt(var2);
            this.state.awayPos = LandRandomPos.getPosAway(this.mob, Math.max(0.0, (double)(9 + var9) - var7), Math.max(1.0, (double)(11 + var9) - var7), 7, var1.position());
            this.state.fleeingTime = 1;
         }

         if (!this.state.tickAndCheckFleeing()) {
            if (this.state.awayPos != null) {
               this.mob.getNavigation().moveTo(this.state.awayPos.x, this.state.awayPos.y, this.state.awayPos.z, (double)var5 * this.speedModifierWhenRepositioning);
               if (this.mob.getNavigation().isDone()) {
                  if (this.state.fleeingTime > 0) {
                     this.state.done = true;
                     return;
                  }

                  this.state.awayPos = null;
               }
            } else {
               this.mob.getNavigation().moveTo((Entity)var1, (double)var5 * this.speedModifierWhenCharging);
               if (var2 < (double)this.targetInRangeRadiusSq || this.mob.getNavigation().isDone()) {
                  double var10 = Math.sqrt(var2);
                  this.state.awayPos = LandRandomPos.getPosAway(this.mob, (double)(6 + var9) - var10, (double)(7 + var9) - var10, 7, var1.position());
               }
            }

         }
      }
   }

   public static class SpearUseState {
      private int engageTime = -1;
      int fleeingTime = -1;
      @Nullable Vec3 awayPos;
      boolean done = false;

      public SpearUseState() {
         super();
      }

      public boolean notEngagedYet() {
         return this.engageTime < 0;
      }

      public void startEngagement(int var1) {
         this.engageTime = var1;
      }

      public boolean tickAndCheckEngagement() {
         if (this.engageTime > 0) {
            --this.engageTime;
            if (this.engageTime == 0) {
               return true;
            }
         }

         return false;
      }

      public boolean tickAndCheckFleeing() {
         if (this.fleeingTime > 0) {
            ++this.fleeingTime;
            if ((double)this.fleeingTime > SpearUseGoal.MAX_FLEEING_TIME) {
               this.done = true;
               return true;
            }
         }

         return false;
      }
   }
}
