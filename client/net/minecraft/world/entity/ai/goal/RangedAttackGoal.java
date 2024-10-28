package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class RangedAttackGoal extends Goal {
   private final Mob mob;
   private final RangedAttackMob rangedAttackMob;
   @Nullable
   private LivingEntity target;
   private int attackTime;
   private final double speedModifier;
   private int seeTime;
   private final int attackIntervalMin;
   private final int attackIntervalMax;
   private final float attackRadius;
   private final float attackRadiusSqr;

   public RangedAttackGoal(RangedAttackMob var1, double var2, int var4, float var5) {
      this(var1, var2, var4, var4, var5);
   }

   public RangedAttackGoal(RangedAttackMob var1, double var2, int var4, int var5, float var6) {
      super();
      this.attackTime = -1;
      if (!(var1 instanceof LivingEntity)) {
         throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
      } else {
         this.rangedAttackMob = var1;
         this.mob = (Mob)var1;
         this.speedModifier = var2;
         this.attackIntervalMin = var4;
         this.attackIntervalMax = var5;
         this.attackRadius = var6;
         this.attackRadiusSqr = var6 * var6;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }
   }

   public boolean canUse() {
      LivingEntity var1 = this.mob.getTarget();
      if (var1 != null && var1.isAlive()) {
         this.target = var1;
         return true;
      } else {
         return false;
      }
   }

   public boolean canContinueToUse() {
      return this.canUse() || this.target.isAlive() && !this.mob.getNavigation().isDone();
   }

   public void stop() {
      this.target = null;
      this.seeTime = 0;
      this.attackTime = -1;
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      double var1 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
      boolean var3 = this.mob.getSensing().hasLineOfSight(this.target);
      if (var3) {
         ++this.seeTime;
      } else {
         this.seeTime = 0;
      }

      if (!(var1 > (double)this.attackRadiusSqr) && this.seeTime >= 5) {
         this.mob.getNavigation().stop();
      } else {
         this.mob.getNavigation().moveTo((Entity)this.target, this.speedModifier);
      }

      this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
      if (--this.attackTime == 0) {
         if (!var3) {
            return;
         }

         float var4 = (float)Math.sqrt(var1) / this.attackRadius;
         float var5 = Mth.clamp(var4, 0.1F, 1.0F);
         this.rangedAttackMob.performRangedAttack(this.target, var5);
         this.attackTime = Mth.floor(var4 * (float)(this.attackIntervalMax - this.attackIntervalMin) + (float)this.attackIntervalMin);
      } else if (this.attackTime < 0) {
         this.attackTime = Mth.floor(Mth.lerp(Math.sqrt(var1) / (double)this.attackRadius, (double)this.attackIntervalMin, (double)this.attackIntervalMax));
      }

   }
}
