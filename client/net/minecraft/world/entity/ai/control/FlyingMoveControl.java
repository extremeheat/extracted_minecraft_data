package net.minecraft.world.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class FlyingMoveControl extends MoveControl {
   private final int maxTurn;
   private final boolean hoversInPlace;

   public FlyingMoveControl(Mob var1, int var2, boolean var3) {
      super(var1);
      this.maxTurn = var2;
      this.hoversInPlace = var3;
   }

   public void tick() {
      if (this.operation == MoveControl.Operation.MOVE_TO) {
         this.operation = MoveControl.Operation.WAIT;
         this.mob.setNoGravity(true);
         double var1 = this.wantedX - this.mob.getX();
         double var3 = this.wantedY - this.mob.getY();
         double var5 = this.wantedZ - this.mob.getZ();
         double var7 = var1 * var1 + var3 * var3 + var5 * var5;
         if (var7 < 2.500000277905201E-7D) {
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
            return;
         }

         float var9 = (float)(Mth.atan2(var5, var1) * 57.2957763671875D) - 90.0F;
         this.mob.setYRot(this.rotlerp(this.mob.getYRot(), var9, 90.0F));
         float var10;
         if (this.mob.isOnGround()) {
            var10 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
         } else {
            var10 = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
         }

         this.mob.setSpeed(var10);
         double var11 = Math.sqrt(var1 * var1 + var5 * var5);
         if (Math.abs(var3) > 9.999999747378752E-6D || Math.abs(var11) > 9.999999747378752E-6D) {
            float var13 = (float)(-(Mth.atan2(var3, var11) * 57.2957763671875D));
            this.mob.setXRot(this.rotlerp(this.mob.getXRot(), var13, (float)this.maxTurn));
            this.mob.setYya(var3 > 0.0D ? var10 : -var10);
         }
      } else {
         if (!this.hoversInPlace) {
            this.mob.setNoGravity(false);
         }

         this.mob.setYya(0.0F);
         this.mob.setZza(0.0F);
      }

   }
}
