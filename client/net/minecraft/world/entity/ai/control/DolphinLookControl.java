package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public class DolphinLookControl extends LookControl {
   private final int maxYRotFromCenter;

   public DolphinLookControl(Mob var1, int var2) {
      super(var1);
      this.maxYRotFromCenter = var2;
   }

   public void tick() {
      if (this.hasWanted) {
         this.hasWanted = false;
         this.mob.yHeadRot = this.rotateTowards(this.mob.yHeadRot, this.getYRotD() + 20.0F, this.yMaxRotSpeed);
         this.mob.xRot = this.rotateTowards(this.mob.xRot, this.getXRotD() + 10.0F, this.xMaxRotAngle);
      } else {
         if (this.mob.getNavigation().isDone()) {
            this.mob.xRot = this.rotateTowards(this.mob.xRot, 0.0F, 5.0F);
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
