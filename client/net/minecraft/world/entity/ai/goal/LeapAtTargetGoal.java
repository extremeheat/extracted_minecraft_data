package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class LeapAtTargetGoal extends Goal {
   private final Mob mob;
   private LivingEntity target;
   private final float yd;

   public LeapAtTargetGoal(Mob var1, float var2) {
      super();
      this.mob = var1;
      this.yd = var2;
      this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   public boolean canUse() {
      if (this.mob.isVehicle()) {
         return false;
      } else {
         this.target = this.mob.getTarget();
         if (this.target == null) {
            return false;
         } else {
            double var1 = this.mob.distanceToSqr(this.target);
            if (!(var1 < 4.0) && !(var1 > 16.0)) {
               if (!this.mob.isOnGround()) {
                  return false;
               } else {
                  return this.mob.getRandom().nextInt(reducedTickDelay(5)) == 0;
               }
            } else {
               return false;
            }
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.mob.isOnGround();
   }

   public void start() {
      Vec3 var1 = this.mob.getDeltaMovement();
      Vec3 var2 = new Vec3(this.target.getX() - this.mob.getX(), 0.0, this.target.getZ() - this.mob.getZ());
      if (var2.lengthSqr() > 1.0E-7) {
         var2 = var2.normalize().scale(0.4).add(var1.scale(0.2));
      }

      this.mob.setDeltaMovement(var2.x, (double)this.yd, var2.z);
   }
}
