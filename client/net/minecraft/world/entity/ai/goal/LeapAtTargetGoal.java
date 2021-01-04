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
            if (var1 >= 4.0D && var1 <= 16.0D) {
               if (!this.mob.onGround) {
                  return false;
               } else {
                  return this.mob.getRandom().nextInt(5) == 0;
               }
            } else {
               return false;
            }
         }
      }
   }

   public boolean canContinueToUse() {
      return !this.mob.onGround;
   }

   public void start() {
      Vec3 var1 = this.mob.getDeltaMovement();
      Vec3 var2 = new Vec3(this.target.x - this.mob.x, 0.0D, this.target.z - this.mob.z);
      if (var2.lengthSqr() > 1.0E-7D) {
         var2 = var2.normalize().scale(0.4D).add(var1.scale(0.2D));
      }

      this.mob.setDeltaMovement(var2.x, (double)this.yd, var2.z);
   }
}
