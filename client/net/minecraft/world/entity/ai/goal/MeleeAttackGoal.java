package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

public class MeleeAttackGoal extends Goal {
   protected final PathfinderMob mob;
   private final double speedModifier;
   private final boolean followingTargetEvenIfNotSeen;
   private Path path;
   private double pathedTargetX;
   private double pathedTargetY;
   private double pathedTargetZ;
   private int ticksUntilNextPathRecalculation;
   private int ticksUntilNextAttack;
   private final int attackInterval = 20;
   private long lastCanUseCheck;
   private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;

   public MeleeAttackGoal(PathfinderMob var1, double var2, boolean var4) {
      super();
      this.mob = var1;
      this.speedModifier = var2;
      this.followingTargetEvenIfNotSeen = var4;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   @Override
   public boolean canUse() {
      long var1 = this.mob.level().getGameTime();
      if (var1 - this.lastCanUseCheck < 20L) {
         return false;
      } else {
         this.lastCanUseCheck = var1;
         LivingEntity var3 = this.mob.getTarget();
         if (var3 == null) {
            return false;
         } else if (!var3.isAlive()) {
            return false;
         } else {
            this.path = this.mob.getNavigation().createPath(var3, 0);
            return this.path != null ? true : this.mob.isWithinMeleeAttackRange(var3);
         }
      }
   }

   @Override
   public boolean canContinueToUse() {
      LivingEntity var1 = this.mob.getTarget();
      if (var1 == null) {
         return false;
      } else if (!var1.isAlive()) {
         return false;
      } else if (!this.followingTargetEvenIfNotSeen) {
         return !this.mob.getNavigation().isDone();
      } else {
         return !this.mob.isWithinRestriction(var1.blockPosition()) ? false : !(var1 instanceof Player) || !var1.isSpectator() && !((Player)var1).isCreative();
      }
   }

   @Override
   public void start() {
      this.mob.getNavigation().moveTo(this.path, this.speedModifier);
      this.mob.setAggressive(true);
      this.ticksUntilNextPathRecalculation = 0;
      this.ticksUntilNextAttack = 0;
   }

   @Override
   public void stop() {
      LivingEntity var1 = this.mob.getTarget();
      if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(var1)) {
         this.mob.setTarget(null);
      }

      this.mob.setAggressive(false);
      this.mob.getNavigation().stop();
   }

   @Override
   public boolean requiresUpdateEveryTick() {
      return true;
   }

   @Override
   public void tick() {
      LivingEntity var1 = this.mob.getTarget();
      if (var1 != null) {
         this.mob.getLookControl().setLookAt(var1, 30.0F, 30.0F);
         this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
         if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(var1))
            && this.ticksUntilNextPathRecalculation <= 0
            && (
               this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0
                  || var1.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0
                  || this.mob.getRandom().nextFloat() < 0.05F
            )) {
            this.pathedTargetX = var1.getX();
            this.pathedTargetY = var1.getY();
            this.pathedTargetZ = var1.getZ();
            this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
            double var2 = this.mob.distanceToSqr(var1);
            if (var2 > 1024.0) {
               this.ticksUntilNextPathRecalculation += 10;
            } else if (var2 > 256.0) {
               this.ticksUntilNextPathRecalculation += 5;
            }

            if (!this.mob.getNavigation().moveTo(var1, this.speedModifier)) {
               this.ticksUntilNextPathRecalculation += 15;
            }

            this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
         }

         this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
         this.checkAndPerformAttack(var1);
      }
   }

   protected void checkAndPerformAttack(LivingEntity var1) {
      if (this.canPerformAttack(var1)) {
         this.resetAttackCooldown();
         this.mob.swing(InteractionHand.MAIN_HAND);
         this.mob.doHurtTarget(var1);
      }
   }

   protected void resetAttackCooldown() {
      this.ticksUntilNextAttack = this.adjustedTickDelay(20);
   }

   protected boolean isTimeToAttack() {
      return this.ticksUntilNextAttack <= 0;
   }

   protected boolean canPerformAttack(LivingEntity var1) {
      return this.isTimeToAttack() && this.mob.isWithinMeleeAttackRange(var1) && this.mob.getSensing().hasLineOfSight(var1);
   }

   protected int getTicksUntilNextAttack() {
      return this.ticksUntilNextAttack;
   }

   protected int getAttackInterval() {
      return this.adjustedTickDelay(20);
   }
}
