package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RangedCrossbowAttackGoal<T extends Monster & RangedAttackMob & CrossbowAttackMob> extends Goal {
   public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
   private final T mob;
   private RangedCrossbowAttackGoal.CrossbowState crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
   private final double speedModifier;
   private final float attackRadiusSqr;
   private int seeTime;
   private int attackDelay;
   private int updatePathDelay;

   public RangedCrossbowAttackGoal(T var1, double var2, float var4) {
      super();
      this.mob = var1;
      this.speedModifier = var2;
      this.attackRadiusSqr = var4 * var4;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   @Override
   public boolean canUse() {
      return this.isValidTarget() && this.isHoldingCrossbow();
   }

   private boolean isHoldingCrossbow() {
      return this.mob.isHolding(Items.CROSSBOW);
   }

   @Override
   public boolean canContinueToUse() {
      return this.isValidTarget() && (this.canUse() || !this.mob.getNavigation().isDone()) && this.isHoldingCrossbow();
   }

   private boolean isValidTarget() {
      return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
   }

   @Override
   public void stop() {
      super.stop();
      this.mob.setAggressive(false);
      this.mob.setTarget(null);
      this.seeTime = 0;
      if (this.mob.isUsingItem()) {
         this.mob.stopUsingItem();
         this.mob.setChargingCrossbow(false);
         CrossbowItem.setCharged(this.mob.getUseItem(), false);
      }
   }

   @Override
   public boolean requiresUpdateEveryTick() {
      return true;
   }

   @Override
   public void tick() {
      LivingEntity var1 = this.mob.getTarget();
      if (var1 != null) {
         boolean var2 = this.mob.getSensing().hasLineOfSight(var1);
         boolean var3 = this.seeTime > 0;
         if (var2 != var3) {
            this.seeTime = 0;
         }

         if (var2) {
            ++this.seeTime;
         } else {
            --this.seeTime;
         }

         double var4 = this.mob.distanceToSqr(var1);
         boolean var6 = (var4 > (double)this.attackRadiusSqr || this.seeTime < 5) && this.attackDelay == 0;
         if (var6) {
            --this.updatePathDelay;
            if (this.updatePathDelay <= 0) {
               this.mob.getNavigation().moveTo(var1, this.canRun() ? this.speedModifier : this.speedModifier * 0.5);
               this.updatePathDelay = PATHFINDING_DELAY_RANGE.sample(this.mob.getRandom());
            }
         } else {
            this.updatePathDelay = 0;
            this.mob.getNavigation().stop();
         }

         this.mob.getLookControl().setLookAt(var1, 30.0F, 30.0F);
         if (this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.UNCHARGED) {
            if (!var6) {
               this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, Items.CROSSBOW));
               this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.CHARGING;
               this.mob.setChargingCrossbow(true);
            }
         } else if (this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.CHARGING) {
            if (!this.mob.isUsingItem()) {
               this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
            }

            int var7 = this.mob.getTicksUsingItem();
            ItemStack var8 = this.mob.getUseItem();
            if (var7 >= CrossbowItem.getChargeDuration(var8)) {
               this.mob.releaseUsingItem();
               this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.CHARGED;
               this.attackDelay = 20 + this.mob.getRandom().nextInt(20);
               this.mob.setChargingCrossbow(false);
            }
         } else if (this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.CHARGED) {
            --this.attackDelay;
            if (this.attackDelay == 0) {
               this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK;
            }
         } else if (this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK && var2) {
            this.mob.performRangedAttack(var1, 1.0F);
            ItemStack var9 = this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, Items.CROSSBOW));
            CrossbowItem.setCharged(var9, false);
            this.crossbowState = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
         }
      }
   }

   private boolean canRun() {
      return this.crossbowState == RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
   }

   static enum CrossbowState {
      UNCHARGED,
      CHARGING,
      CHARGED,
      READY_TO_ATTACK;

      private CrossbowState() {
      }
   }
}
