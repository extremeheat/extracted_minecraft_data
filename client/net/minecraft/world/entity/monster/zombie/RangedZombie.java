package net.minecraft.world.entity.monster.zombie;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.IntSupplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;

public abstract class RangedZombie extends Zombie implements RangedAttackMob {
   private static final float MELEE_MODE_DISTANCE_SQR = 9.0F;
   private static final float RANGED_MODE_DISTANCE_SQR = 25.0F;
   private float rangedAttackUncertainty = -1.0F;
   private boolean isRanged;

   public RangedZombie(final EntityType<? extends Zombie> type, final Level level) {
      super(type, level);
   }

   @VisibleForTesting
   public boolean isRanged() {
      return this.isRanged;
   }

   public float rangedAttackUncertainty(final Level level) {
      return this.rangedAttackUncertainty < 0.0F ? RangedAttackMob.super.rangedAttackUncertainty(level) : this.rangedAttackUncertainty;
   }

   @VisibleForTesting
   public void setRangedAttackUncertainty(final float rangedAttackUncertainty) {
      this.rangedAttackUncertainty = rangedAttackUncertainty;
   }

   @VisibleForTesting
   public abstract Item getRangedWeaponType();

   protected abstract Projectile getProjectile(final Level level, final LivingEntity owner, final ItemStack item);

   protected abstract SoundEvent getRangedAttackSound();

   public void performRangedAttack(final LivingEntity target, final float power) {
      ItemStack mainHandItem = this.getMainHandItem();
      ItemStack itemStack = mainHandItem.is(this.getRangedWeaponType()) ? mainHandItem : new ItemStack(this.getRangedWeaponType());
      Projectile projectile = this.getProjectile(this.level(), this, itemStack);
      double xd = target.getX() - this.getX();
      double yd = target.getY(0.3333333333333333) - projectile.getY();
      double zd = target.getZ() - this.getZ();
      double distanceToTarget = Math.sqrt(xd * xd + zd * zd);
      Level var15 = this.level();
      if (var15 instanceof ServerLevel serverLevel) {
         Projectile.spawnProjectileUsingShoot(projectile, serverLevel, itemStack, xd, yd + distanceToTarget * projectile.getTrajectoryCorrectionFactor(), zd, 1.6F, this.rangedAttackUncertainty(serverLevel));
      }

      this.playSound(this.getRangedAttackSound(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
   }

   protected boolean shouldDoRangedAttack() {
      if (!this.getMainHandItem().is(this.getRangedWeaponType())) {
         return false;
      } else {
         LivingEntity target = this.getTarget();
         if (target != null && target.isAlive()) {
            float rangeThreshold = this.isRanged ? 9.0F : 25.0F;
            if (this.distanceToSqr(target) >= (double)rangeThreshold) {
               return true;
            } else if (this.isWithinMeleeAttackRange(target)) {
               return false;
            } else {
               Path path = this.navigation.getPath() != null ? this.navigation.getPath() : this.navigation.createPath(target, 0);
               return path != null && path.getTarget().equals(target.blockPosition()) && !path.canReach();
            }
         } else {
            return false;
         }
      }
   }

   protected static class RangedZombieRangedAttackGoal extends RangedAttackGoal {
      private final RangedZombie zombie;
      private final boolean chargeWeapon;
      private final boolean stopWhenInRange;

      public RangedZombieRangedAttackGoal(final RangedAttackMob mob, final double speedModifier, final IntSupplier attackIntervalMin, final IntSupplier attackIntervalMax, final float attackRadius, final boolean chargeWeapon, final boolean stopWhenInRange) {
         super(mob, speedModifier, attackIntervalMin, attackIntervalMax, attackRadius);
         this.zombie = (RangedZombie)mob;
         this.chargeWeapon = chargeWeapon;
         this.stopWhenInRange = stopWhenInRange;
      }

      public boolean canUse() {
         return super.canUse() && this.zombie.shouldDoRangedAttack();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.zombie.shouldDoRangedAttack();
      }

      public void start() {
         super.start();
         this.zombie.setAggressive(true);
         if (this.chargeWeapon) {
            this.zombie.startUsingItem(InteractionHand.MAIN_HAND);
         }

         this.zombie.isRanged = true;
      }

      public void stop() {
         super.stop();
         this.zombie.setAggressive(false);
         if (this.chargeWeapon) {
            this.zombie.stopUsingItem();
         }

         this.zombie.isRanged = false;
      }

      protected boolean stopWhenInRange() {
         return this.stopWhenInRange;
      }
   }

   protected static class RangedZombieMeleeAttackGoal extends ZombieAttackGoal {
      private final RangedZombie zombie;
      private final boolean chargeWeapon;

      public RangedZombieMeleeAttackGoal(final Zombie mob, final double speedModifier, final boolean trackTarget, final int attackInterval, final boolean chargeWeapon) {
         super(mob, speedModifier, trackTarget, attackInterval);
         this.zombie = (RangedZombie)mob;
         this.chargeWeapon = chargeWeapon;
      }

      public void start() {
         super.start();
         if (this.chargeWeapon && this.zombie.getMainHandItem().has(DataComponents.WEAPON)) {
            this.zombie.startUsingItem(InteractionHand.MAIN_HAND);
         }

      }

      public void stop() {
         super.stop();
         if (this.chargeWeapon && this.zombie.getMainHandItem().has(DataComponents.WEAPON)) {
            this.zombie.stopUsingItem();
         }

      }
   }
}
