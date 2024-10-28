package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public class SmoothSwimmingLookControl extends LookControl {
   private final int maxYRotFromCenter;
   private static final int HEAD_TILT_X = 10;
   private static final int HEAD_TILT_Y = 20;

   public SmoothSwimmingLookControl(Mob var1, int var2) {
      super(var1);
      this.maxYRotFromCenter = var2;
   }

   public void tick() {
      if (this.lookAtCooldown > 0) {
         --this.lookAtCooldown;
         this.getYRotD().ifPresent((var1x) -> {
            this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, var1x + 20.0F, this.yMaxRotSpeed);
         });
         this.getXRotD().ifPresent((var1x) -> {
            this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), var1x + 10.0F, this.xMaxRotAngle));
         });
      } else {
         if (this.mob.getNavigation().isDone()) {
            this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), 0.0F, 5.0F));
         }

         this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.mob.yBodyRot, this.yMaxRotSpeed);
      }

      float var1 = Mth.wrapDegrees(this.mob.yHeadRot - this.mob.yBodyRot);
      Mob var10000;
      if (var1 < (float)(-this.maxYRotFromCenter)) {
         var10000 = this.mob;
         var10000.yBodyRot -= 4.0F;
      } else if (var1 > (float)this.maxYRotFromCenter) {
         var10000 = this.mob;
         var10000.yBodyRot += 4.0F;
      }

   }
}
