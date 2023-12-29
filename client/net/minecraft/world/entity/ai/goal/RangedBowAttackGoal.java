package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;

public class RangedBowAttackGoal<T extends Monster & RangedAttackMob> extends Goal {
   private final T mob;
   private final double speedModifier;
   private int attackIntervalMin;
   private final float attackRadiusSqr;
   private int attackTime = -1;
   private int seeTime;
   private boolean strafingClockwise;
   private boolean strafingBackwards;
   private int strafingTime = -1;

   public RangedBowAttackGoal(T var1, double var2, int var4, float var5) {
      super();
      this.mob = var1;
      this.speedModifier = var2;
      this.attackIntervalMin = var4;
      this.attackRadiusSqr = var5 * var5;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public void setMinAttackInterval(int var1) {
      this.attackIntervalMin = var1;
   }

   @Override
   public boolean canUse() {
      return this.mob.getTarget() == null ? false : this.isHoldingBow();
   }

   protected boolean isHoldingBow() {
      return this.mob.isHolding(Items.BOW);
   }

   @Override
   public boolean canContinueToUse() {
      return (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingBow();
   }

   @Override
   public void start() {
      super.start();
      this.mob.setAggressive(true);
   }

   @Override
   public void stop() {
      super.stop();
      this.mob.setAggressive(false);
      this.seeTime = 0;
      this.attackTime = -1;
      this.mob.stopUsingItem();
   }

   @Override
   public boolean requiresUpdateEveryTick() {
      return true;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void tick() {
      LivingEntity var1 = this.mob.getTarget();
      if (var1 != null) {
         double var2 = this.mob.distanceToSqr(var1.getX(), var1.getY(), var1.getZ());
         boolean var4 = this.mob.getSensing().hasLineOfSight(var1);
         boolean var5 = this.seeTime > 0;
         if (var4 != var5) {
            this.seeTime = 0;
         }

         if (var4) {
            ++this.seeTime;
         } else {
            --this.seeTime;
         }

         if (!(var2 > (double)this.attackRadiusSqr) && this.seeTime >= 20) {
            this.mob.getNavigation().stop();
            ++this.strafingTime;
         } else {
            this.mob.getNavigation().moveTo(var1, this.speedModifier);
            this.strafingTime = -1;
         }

         if (this.strafingTime >= 20) {
            if ((double)this.mob.getRandom().nextFloat() < 0.3) {
               this.strafingClockwise = !this.strafingClockwise;
            }

            if ((double)this.mob.getRandom().nextFloat() < 0.3) {
               this.strafingBackwards = !this.strafingBackwards;
            }

            this.strafingTime = 0;
         }

         if (this.strafingTime > -1) {
            if (var2 > (double)(this.attackRadiusSqr * 0.75F)) {
               this.strafingBackwards = false;
            } else if (var2 < (double)(this.attackRadiusSqr * 0.25F)) {
               this.strafingBackwards = true;
            }

            this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
            Entity var7 = this.mob.getControlledVehicle();
            if (var7 instanceof Mob var6) {
               var6.lookAt(var1, 30.0F, 30.0F);
            }

            this.mob.lookAt(var1, 30.0F, 30.0F);
         } else {
            this.mob.getLookControl().setLookAt(var1, 30.0F, 30.0F);
         }

         if (this.mob.isUsingItem()) {
            if (!var4 && this.seeTime < -60) {
               this.mob.stopUsingItem();
            } else if (var4) {
               int var8 = this.mob.getTicksUsingItem();
               if (var8 >= 20) {
                  this.mob.stopUsingItem();
                  this.mob.performRangedAttack(var1, BowItem.getPowerForTime(var8));
                  this.attackTime = this.attackIntervalMin;
               }
            }
         } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
            this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, Items.BOW));
         }
      }
   }
}
