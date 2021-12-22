package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SmoothSwimmingMoveControl extends MoveControl {
   private final int maxTurnX;
   private final int maxTurnY;
   private final float inWaterSpeedModifier;
   private final float outsideWaterSpeedModifier;
   private final boolean applyGravity;

   public SmoothSwimmingMoveControl(Mob var1, int var2, int var3, float var4, float var5, boolean var6) {
      super(var1);
      this.maxTurnX = var2;
      this.maxTurnY = var3;
      this.inWaterSpeedModifier = var4;
      this.outsideWaterSpeedModifier = var5;
      this.applyGravity = var6;
   }

   public void tick() {
      if (this.applyGravity && this.mob.isInWater()) {
         this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
      }

      if (this.operation == MoveControl.Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
         double var1 = this.wantedX - this.mob.getX();
         double var3 = this.wantedY - this.mob.getY();
         double var5 = this.wantedZ - this.mob.getZ();
         double var7 = var1 * var1 + var3 * var3 + var5 * var5;
         if (var7 < 2.500000277905201E-7D) {
            this.mob.setZza(0.0F);
         } else {
            float var9 = (float)(Mth.atan2(var5, var1) * 57.2957763671875D) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), var9, (float)this.maxTurnY));
            this.mob.yBodyRot = this.mob.getYRot();
            this.mob.yHeadRot = this.mob.getYRot();
            float var10 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            if (this.mob.isInWater()) {
               this.mob.setSpeed(var10 * this.inWaterSpeedModifier);
               double var11 = Math.sqrt(var1 * var1 + var5 * var5);
               float var13;
               if (Math.abs(var3) > 9.999999747378752E-6D || Math.abs(var11) > 9.999999747378752E-6D) {
                  var13 = -((float)(Mth.atan2(var3, var11) * 57.2957763671875D));
                  var13 = Mth.clamp(Mth.wrapDegrees(var13), (float)(-this.maxTurnX), (float)this.maxTurnX);
                  this.mob.setXRot(this.rotlerp(this.mob.getXRot(), var13, 5.0F));
               }

               var13 = Mth.cos(this.mob.getXRot() * 0.017453292F);
               float var14 = Mth.sin(this.mob.getXRot() * 0.017453292F);
               this.mob.zza = var13 * var10;
               this.mob.yya = -var14 * var10;
            } else {
               this.mob.setSpeed(var10 * this.outsideWaterSpeedModifier);
            }

         }
      } else {
         this.mob.setSpeed(0.0F);
         this.mob.setXxa(0.0F);
         this.mob.setYya(0.0F);
         this.mob.setZza(0.0F);
      }
   }
}
