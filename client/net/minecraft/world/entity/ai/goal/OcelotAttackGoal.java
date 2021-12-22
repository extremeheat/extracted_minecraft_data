package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class OcelotAttackGoal extends Goal {
   private final Mob mob;
   private LivingEntity target;
   private int attackTime;

   public OcelotAttackGoal(Mob var1) {
      super();
      this.mob = var1;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      LivingEntity var1 = this.mob.getTarget();
      if (var1 == null) {
         return false;
      } else {
         this.target = var1;
         return true;
      }
   }

   public boolean canContinueToUse() {
      if (!this.target.isAlive()) {
         return false;
      } else if (this.mob.distanceToSqr(this.target) > 225.0D) {
         return false;
      } else {
         return !this.mob.getNavigation().isDone() || this.canUse();
      }
   }

   public void stop() {
      this.target = null;
      this.mob.getNavigation().stop();
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
      double var1 = (double)(this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F);
      double var3 = this.mob.distanceToSqr(this.target.getX(), this.target.getY(), this.target.getZ());
      double var5 = 0.8D;
      if (var3 > var1 && var3 < 16.0D) {
         var5 = 1.33D;
      } else if (var3 < 225.0D) {
         var5 = 0.6D;
      }

      this.mob.getNavigation().moveTo((Entity)this.target, var5);
      this.attackTime = Math.max(this.attackTime - 1, 0);
      if (!(var3 > var1)) {
         if (this.attackTime <= 0) {
            this.attackTime = 20;
            this.mob.doHurtTarget(this.target);
         }
      }
   }
}
