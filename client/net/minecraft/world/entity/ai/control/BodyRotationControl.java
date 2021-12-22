package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public class BodyRotationControl implements Control {
   private final Mob mob;
   private static final int HEAD_STABLE_ANGLE = 15;
   private static final int DELAY_UNTIL_STARTING_TO_FACE_FORWARD = 10;
   private static final int HOW_LONG_IT_TAKES_TO_FACE_FORWARD = 10;
   private int headStableTime;
   private float lastStableYHeadRot;

   public BodyRotationControl(Mob var1) {
      super();
      this.mob = var1;
   }

   public void clientTick() {
      if (this.isMoving()) {
         this.mob.yBodyRot = this.mob.getYRot();
         this.rotateHeadIfNecessary();
         this.lastStableYHeadRot = this.mob.yHeadRot;
         this.headStableTime = 0;
      } else {
         if (this.notCarryingMobPassengers()) {
            if (Math.abs(this.mob.yHeadRot - this.lastStableYHeadRot) > 15.0F) {
               this.headStableTime = 0;
               this.lastStableYHeadRot = this.mob.yHeadRot;
               this.rotateBodyIfNecessary();
            } else {
               ++this.headStableTime;
               if (this.headStableTime > 10) {
                  this.rotateHeadTowardsFront();
               }
            }
         }

      }
   }

   private void rotateBodyIfNecessary() {
      this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, (float)this.mob.getMaxHeadYRot());
   }

   private void rotateHeadIfNecessary() {
      this.mob.yHeadRot = Mth.rotateIfNecessary(this.mob.yHeadRot, this.mob.yBodyRot, (float)this.mob.getMaxHeadYRot());
   }

   private void rotateHeadTowardsFront() {
      int var1 = this.headStableTime - 10;
      float var2 = Mth.clamp((float)var1 / 10.0F, 0.0F, 1.0F);
      float var3 = (float)this.mob.getMaxHeadYRot() * (1.0F - var2);
      this.mob.yBodyRot = Mth.rotateIfNecessary(this.mob.yBodyRot, this.mob.yHeadRot, var3);
   }

   private boolean notCarryingMobPassengers() {
      return !(this.mob.getFirstPassenger() instanceof Mob);
   }

   private boolean isMoving() {
      double var1 = this.mob.getX() - this.mob.xo;
      double var3 = this.mob.getZ() - this.mob.zo;
      return var1 * var1 + var3 * var3 > 2.500000277905201E-7D;
   }
}
