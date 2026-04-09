package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SmoothSwimmingMoveControl<T extends Mob> extends MoveControl<T> {
   private static final float FULL_SPEED_TURN_THRESHOLD = 10.0F;
   private static final float STOP_TURN_THRESHOLD = 60.0F;
   private final int maxTurnX;
   private final int maxTurnY;
   private final float inWaterSpeedModifier;
   private final float outsideWaterSpeedModifier;
   private final boolean applyGravity;

   public SmoothSwimmingMoveControl(final T mob, final int maxTurnX, final int maxTurnY, final float inWaterSpeedModifier, final float outsideWaterSpeedModifier, final boolean applyGravity) {
      super(mob);
      this.maxTurnX = maxTurnX;
      this.maxTurnY = maxTurnY;
      this.inWaterSpeedModifier = inWaterSpeedModifier;
      this.outsideWaterSpeedModifier = outsideWaterSpeedModifier;
      this.applyGravity = applyGravity;
   }

   public void tick() {
      if (this.applyGravity && this.mob.isInWater()) {
         this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, 0.005, 0.0));
      }

      if (this.operation == MoveControl.Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
         double xd = this.wantedX - this.mob.getX();
         double yd = this.wantedY - this.mob.getY();
         double zd = this.wantedZ - this.mob.getZ();
         double dd = xd * xd + yd * yd + zd * zd;
         if (dd < 2.500000277905201E-7) {
            this.mob.setZza(0.0F);
         } else {
            float yRotD = (float)(Mth.atan2(zd, xd) * 57.2957763671875) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yRotD, (float)this.maxTurnY));
            this.mob.yBodyRot = this.mob.getYRot();
            this.mob.yHeadRot = this.mob.getYRot();
            float speed = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            if (this.mob.isInWater()) {
               this.mob.setSpeed(speed * this.inWaterSpeedModifier);
               double sqrt = Math.sqrt(xd * xd + zd * zd);
               if (Math.abs(yd) > 9.999999747378752E-6 || Math.abs(sqrt) > 9.999999747378752E-6) {
                  float xRotD = -((float)(Mth.atan2(yd, sqrt) * 57.2957763671875));
                  xRotD = Mth.clamp(Mth.wrapDegrees(xRotD), (float)(-this.maxTurnX), (float)this.maxTurnX);
                  this.mob.setXRot(this.rotateTowards(this.mob.getXRot(), xRotD, 5.0F));
               }

               float cos = Mth.cos((double)(this.mob.getXRot() * 0.017453292F));
               float sin = Mth.sin((double)(this.mob.getXRot() * 0.017453292F));
               this.mob.zza = cos * speed;
               this.mob.yya = -sin * speed;
            } else {
               float leftToTurn = Math.abs(Mth.wrapDegrees(this.mob.getYRot() - yRotD));
               float turningSpeedFactor = getTurningSpeedFactor(leftToTurn);
               this.mob.setSpeed(speed * this.outsideWaterSpeedModifier * turningSpeedFactor);
            }

         }
      } else {
         this.mob.setSpeed(0.0F);
         this.mob.setXxa(0.0F);
         this.mob.setYya(0.0F);
         this.mob.setZza(0.0F);
      }
   }

   private static float getTurningSpeedFactor(final float leftToTurn) {
      return 1.0F - Mth.clamp((leftToTurn - 10.0F) / 50.0F, 0.0F, 1.0F);
   }
}
